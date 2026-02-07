package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.Especialidade;
import com.vivamamadsc.vivamamadsc.Medico;
import com.vivamamadsc.vivamamadsc.base.BaseTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class MedicoValidatorTestSegundaEntrega extends BaseTest {

    private Medico medicoValido() {
        Medico m = new Medico();

        m.setNome("Dra Ana");
        m.setEmail("ana@teste.com");
        m.setCpf("05980165037");
        m.setSenha("Abc@12345");

        // CRM válido
        Crm c = new Crm();
        c.setEstado("PE");
        c.setNumero("123");
        c.setMedico(m);
        m.setCrm(c);

        // especialidade válida
        Especialidade esp = new Especialidade();
        esp.setNome("ginecologista");
        m.getEspecialidades().add(esp);

        return m;
    }

    private ConstraintViolationException unwrapCVE(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof ConstraintViolationException) return (ConstraintViolationException) t;
            t = t.getCause();
        }
        return null;
    }

    // 1) CRM obrigatório + min de especialidades (lista vazia)
    @Test
    public void persistirMedicoSemCrmESemEspecialidades_deveFalharNoFlush() {
        Medico m = medicoValido();

        m.setCrm(null);                // {medico.crm.obrigatorio}
        m.getEspecialidades().clear(); // {medico.especialidades.min}

        try {
            em.persist(m);
            em.flush();
            fail("Era esperado ConstraintViolationException no flush()");
        } catch (RuntimeException ex) {

            ConstraintViolationException cve = unwrapCVE(ex);
            assertTrue("Não veio ConstraintViolationException. Veio: "
                    + ex.getClass().getName()
                    + " | cause: "
                    + (ex.getCause() == null ? "null" : ex.getCause().getClass().getName())
                    + " | msg: " + ex.getMessage(),
                    cve != null);

            Set<String> t = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .collect(Collectors.toSet());

            assertTrue("Esperava {medico.crm.obrigatorio}, veio: " + t,
                    t.contains("{medico.crm.obrigatorio}"));

            assertTrue("Esperava {medico.especialidades.min}, veio: " + t,
                    t.contains("{medico.especialidades.min}"));

            assertNull(m.getId());
        }
    }

    // 2) Item nulo (lista com 1 elemento null)
    @Test
    public void persistirMedicoComEspecialidadeNula_deveFalharNoFlush() {
        Medico m = medicoValido();

        // mantém tamanho >= 1, mas coloca null
        m.getEspecialidades().clear();
        m.getEspecialidades().add(null); // {medico.especialidades.item.obrigatorio}

        try {
            em.persist(m);
            em.flush();
            fail("Era esperado ConstraintViolationException no flush()");
        } catch (RuntimeException ex) {

            ConstraintViolationException cve = unwrapCVE(ex);
            assertTrue("Não veio ConstraintViolationException. Veio: "
                    + ex.getClass().getName()
                    + " | cause: "
                    + (ex.getCause() == null ? "null" : ex.getCause().getClass().getName())
                    + " | msg: " + ex.getMessage(),
                    cve != null);

            Set<String> t = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .collect(Collectors.toSet());

            assertTrue("Esperava {medico.especialidades.item.obrigatorio}, veio: " + t,
                    t.contains("{medico.especialidades.item.obrigatorio}"));

            assertNull(m.getId());
        }
    }
}
