/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Mensagem;
import com.vivamamadsc.vivamamadsc.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import static junit.framework.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author helena
 */
public class MensagemValidatorTestSegundaEntrega {

    private static Validator validator;

    private static class MensagemTeste extends Mensagem { }
    private static class ConversaTeste extends Conversa { }
    private static class UsuarioTeste extends Usuario { }

    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("pt", "BR"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private MensagemTeste mensagemValida() {
        MensagemTeste m = new MensagemTeste();
        m.setConversa(new ConversaTeste());
        m.setRemetente(new UsuarioTeste());
        m.setTexto("Olá! Tudo bem?");
        m.setEnviadoEm(new Date());
        return m;
    }

    private Set<String> templates(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.toSet());
    }

    private void assertHas(Set<? extends ConstraintViolation<?>> violations, String template) {
        Set<String> t = templates(violations);
        assertTrue("Esperava violação com template: " + template + " mas veio: " + t, t.contains(template));
    }

    // -------------------------
    // CONVERSA
    // -------------------------
    @Test
    public void conversaDeveSerObrigatoria() {
        MensagemTeste m = mensagemValida();
        m.setConversa(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertTrue("Esperava mensagem de erro da conversa. Veio: " + v,
                v.stream().anyMatch(cv -> "Conversa é obrigatória".equals(cv.getMessage())));
    }

    // teste REMETENTE
    @Test
    public void remetenteDeveSerObrigatorio() {
        MensagemTeste m = mensagemValida();
        m.setRemetente(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.remetente.obrigatorio}");
    }

    // teste TEXTO
    @Test
    public void textoDeveSerObrigatorioNotBlank() {
        MensagemTeste m = mensagemValida();
        m.setTexto("   ");

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.texto.obrigatorio}");
    }

    @Test
    public void textoDeveRespeitarMax5000() {
        MensagemTeste m = mensagemValida();
        m.setTexto("a".repeat(5001));

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.texto.max}");
    }

    // teste ENVIADO EM

    @Test
    public void enviadoEmDeveSerObrigatorio() {
        MensagemTeste m = mensagemValida();
        m.setEnviadoEm(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.enviadoEm.obrigatorio}");
    }

    @Test
    public void enviadoEmNaoPodeSerFuturo() {
        MensagemTeste m = mensagemValida();
        m.setEnviadoEm(new Date(System.currentTimeMillis() + 60_000)); // +1 minuto

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.enviadoEm.passadoOuPresente}");
    }

    // teste NOME ANEXO
    @Test
    public void nomeAnexoDeveRespeitarMax255() {
        MensagemTeste m = mensagemValida();
        m.setNomeAnexo("a".repeat(256));

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.nomeAnexo.max}");
    }

    // CONSISTÊNCIA: anexo <-> nomeAnexo
    @Test
    public void seTiverAnexoDeveTerNomeAnexo() {
        MensagemTeste m = mensagemValida();
        m.setAnexo(new byte[]{1, 2, 3});
        m.setNomeAnexo(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.anexo.consistente}");
    }

    @Test
    public void seTiverNomeAnexoDeveTerAnexo() {
        MensagemTeste m = mensagemValida();
        m.setAnexo(null);
        m.setNomeAnexo("arquivo.pdf");

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.anexo.consistente}");
    }

    @Test
    public void anexoConsistenteQuandoAmbosAusentes() {
        MensagemTeste m = mensagemValida();
        m.setAnexo(null);
        m.setNomeAnexo(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertTrue("Não deveria haver violações. Veio: " + templates(v), v.isEmpty());
    }

    @Test
    public void anexoConsistenteQuandoAmbosPresentes() {
        MensagemTeste m = mensagemValida();
        m.setAnexo(new byte[]{9, 9, 9});
        m.setNomeAnexo("imagem.png");

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertTrue("Não deveria haver violações. Veio: " + templates(v), v.isEmpty());
    }

    //teste TAMANHO DO ANEXO
    @Test
    public void anexoDeveRespeitarMax5MB() {
        MensagemTeste m = mensagemValida();
        byte[] grande = new byte[5_242_881];
        m.setAnexo(grande);
        m.setNomeAnexo("grande.bin"); 

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.anexo.max}");
    }  
}
