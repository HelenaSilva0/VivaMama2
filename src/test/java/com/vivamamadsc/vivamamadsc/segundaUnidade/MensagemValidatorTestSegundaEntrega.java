/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Mensagem;
import com.vivamamadsc.vivamamadsc.Usuario;
import com.vivamamadsc.vivamamadsc.base.BaseTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 *
 * @author helena
 */
public class MensagemValidatorTestSegundaEntrega extends BaseTest {

    private static class MensagemTeste extends Mensagem {
    }

    private static class ConversaTeste extends Conversa {
    }

    private static class UsuarioTeste extends Usuario {
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

    //teste GERAL
    @Test(expected = ConstraintViolationException.class)
    public void DeveDispararTodasAsValidacoes() {
        Mensagem m = new Mensagem();

        try {

            m.setConversa(null);
            m.setRemetente(null);
            m.setTexto("   ");
            m.setEnviadoEm(new Date(System.currentTimeMillis() + 60_000));

            m.setNomeAnexo(null);
            m.setAnexo(new byte[5_242_881]);

            em.persist(m);
            em.flush();
            fail("Era esperado ConstraintViolationException no flush()");
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> v = ex.getConstraintViolations();

            v.forEach(violation -> {
                String msg = violation.getRootBeanClass() + "." + violation.getPropertyPath()
                        + ": " + violation.getMessageTemplate();

                assertThat(msg, anyOf(
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.conversa: {mensagem.conversa.obrigatoria}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.remetente: {mensagem.remetente.obrigatorio}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.texto: {mensagem.texto.obrigatorio}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.enviadoEm: {mensagem.enviadoEm.passadoOuPresente}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.anexo: {mensagem.anexo.max}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.nomeAnexo: {mensagem.nomeAnexo.obrigatorioQuandoAnexo}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.anexo: {mensagem.anexo.obrigatorioQuandoNome}"),
                        startsWith("class com.vivamamadsc.vivamamadsc.Mensagem.: {mensagem.anexo.consistente}")
                ));
            });
            Set<String> templates = v.stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .collect(Collectors.toSet());

            // essenciais
            assertThat(templates, hasItem("{mensagem.conversa.obrigatoria}"));
            assertThat(templates, hasItem("{mensagem.remetente.obrigatorio}"));
            assertThat(templates, hasItem("{mensagem.texto.obrigatorio}"));
            assertThat(templates, hasItem("{mensagem.enviadoEm.passadoOuPresente}"));
            assertThat(templates, hasItem("{mensagem.anexo.max}"));

            assertTrue(
                    templates.contains("{mensagem.nomeAnexo.obrigatorioQuandoAnexo}")
                    || templates.contains("{mensagem.anexo.obrigatorioQuandoNome}")
            );

            assertNull(m.getId());
            throw ex;
        }
    }

    // CONVERSA
    @Test
    public void conversaDeveSerObrigatoria() {
        MensagemTeste m = mensagemValida();
        m.setConversa(null);

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertTrue("Esperava mensagem de erro da conversa. Veio: " + v,
                v.stream().anyMatch(cv -> "{mensagem.conversa.obrigatoria}".equals(cv.getMessageTemplate())));
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

        assertHas(v, "{mensagem.nomeAnexo.obrigatorioQuandoAnexo}");
    }

    @Test
    public void seTiverNomeAnexoDeveTerAnexo() {
        MensagemTeste m = mensagemValida();
        m.setAnexo(null);
        m.setNomeAnexo("arquivo.pdf");

        Set<ConstraintViolation<MensagemTeste>> v = validator.validate(m);

        assertHas(v, "{mensagem.anexo.obrigatorioQuandoNome}");
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
