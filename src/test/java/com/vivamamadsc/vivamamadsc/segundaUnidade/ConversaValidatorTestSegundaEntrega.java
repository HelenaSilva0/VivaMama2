/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
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
import org.junit.Test;

/**
 *
 * @author helena
 */
public class ConversaValidatorTestSegundaEntrega extends BaseTest {

    private static class UsuarioTeste extends Usuario {
    }

    private Conversa conversaValida() {
        Conversa c = new Conversa();
        c.setAssunto("Assunto válido");
        c.setCriadoEm(new Date());
        c.getParticipantes().add(new UsuarioTeste());
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

    private ConstraintViolationException unwrapCVE(Throwable ex) {
        Throwable cur = ex;
        while (cur != null) {
            if (cur instanceof ConstraintViolationException constraintViolationException) {
                return constraintViolationException;
            }
            cur = cur.getCause();
        }
        return null;
    }

    //teste GERAL
    @Test(expected = ConstraintViolationException.class)
    public void persistirConversaInvalidaDeveDispararBeanValidationNoFlush() {
        Conversa c = conversaValida();

        c.setAssunto("   ");
        c.getParticipantes().clear();
        c.setCriadoEm(null);

        try {
            em.persist(c);
            em.flush();
            fail("Era esperado ConstraintViolationException no flush()");
        } catch (RuntimeException ex) {
            ConstraintViolationException cve = unwrapCVE(ex);
            if (cve == null) {
                throw ex;
            }

            Set<ConstraintViolation<?>> v = cve.getConstraintViolations();

            Set<String> t = v.stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .collect(Collectors.toSet());

            assertTrue("Esperava {conversa.assunto.obrigatorio}, veio: " + t,
                    t.contains("{conversa.assunto.obrigatorio}"));

            assertTrue("Esperava {conversa.participantes.min}, veio: " + t,
                    t.contains("{conversa.participantes.min}"));

            assertNull(c.getId());
            throw cve;
        }
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

    @Test
    public void conversaDeveTerPeloMenosDoisParticipantes() {
        Conversa c = new Conversa();
        c.setAssunto("Assunto válido");
        c.setCriadoEm(new Date());
        c.getParticipantes().add(new UsuarioTeste());

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.participantes.min2}");
    }

    @Test
    public void conversaNaoDeveTerParticipantesDuplicados() {
        Conversa c = new Conversa();
        c.setAssunto("Assunto válido");
        c.setCriadoEm(new Date());

        UsuarioTeste u = new UsuarioTeste();
        c.getParticipantes().add(u);
        c.getParticipantes().add(u);

        Set<ConstraintViolation<Conversa>> v = validator.validate(c);

        assertHas(v, "{conversa.participantes.duplicados}");
    }
}
