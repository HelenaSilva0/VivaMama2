/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
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
public class ConversaValidatorTestSegundaEntrega {
    private static Validator validator;

    private static class UsuarioTeste extends Usuario { }

    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("pt", "BR"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Conversa conversaValida() {
        Conversa c = new Conversa();
        c.setAssunto("Assunto válido");
        c.setCriadoEm(new Date());
        c.getParticipantes().add(new UsuarioTeste());
        return c;
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

    // teste ASSUNTO

    @Test
    public void assuntoDeveSerObrigatorio_notBlank() {
        Conversa c = conversaValida();
        c.setAssunto("   ");

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.assunto.obrigatorio}");
    }

    @Test
    public void assuntoDeveRespeitarMax200() {
        Conversa c = conversaValida();
        c.setAssunto("a".repeat(201));

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.assunto.max}");
    }

    // teste CRIADO EM

    @Test
    public void criadoEmDeveSerObrigatorio() {
        Conversa c = conversaValida();
        c.setCriadoEm(null);

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.criadoEm.obrigatorio}");
    }

    // teste PARTICIPANTES

    @Test
    public void deveTerPeloMenosUmParticipante() {
        Conversa c = conversaValida();
        c.getParticipantes().clear();

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.participantes.min}");
    }

    @Test
    public void participantesNaoPodemConterNull() {
        Conversa c = conversaValida();
        c.getParticipantes().add(null); 

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.participantes.item.obrigatorio}");
    }
}
