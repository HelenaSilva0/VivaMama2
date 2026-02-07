package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Crm;
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

public class CrmValidatorTestSegundaEntrega extends BaseTest {

    private Crm crmValido() {
        Crm c = new Crm();
        c.setEstado("PE");
        c.setNumero("123");
        c.setMedico(new Medico());
        return c;
    }

    private ConstraintViolationException unwrapCVE(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof ConstraintViolationException) return (ConstraintViolationException) t;
            t = t.getCause();
        }
        return null;
    }

    @Test
    public void persistirCrmInvalidoDeveDispararBeanValidationNoFlush() {
        Crm c = crmValido();

        c.setNumero("12A3");  // -> {crm.numero.formato} 
        c.setEstado("P1");    // -> {crm.estado.formato}
        c.setMedico(null);    // -> {crm.medico.obrigatorio}

        try {
            em.persist(c);
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

            assertTrue("Esperava {crm.numero.formato}, veio: " + t,
                    t.contains("{crm.numero.formato}"));

            assertTrue("Esperava {crm.estado.formato}, veio: " + t,
                    t.contains("{crm.estado.formato}"));

            assertTrue("Esperava {crm.medico.obrigatorio}, veio: " + t,
                    t.contains("{crm.medico.obrigatorio}"));

            assertNull(c.getId());
        }
    }
}
