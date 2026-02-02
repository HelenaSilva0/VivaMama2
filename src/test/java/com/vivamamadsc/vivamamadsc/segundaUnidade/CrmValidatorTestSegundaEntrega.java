package com.vivamamadsc.vivamamadsc.segundaUnidade;
import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.Medico;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import static junit.framework.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author EmillY Maria
 */
public class CrmValidatorTestSegundaEntrega {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("pt", "BR"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Crm crmValido() {
        Crm c = new Crm();
        c.setEstado("PE");
        c.setNumero("PE123");
        c.setMedico(new Medico());
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

    // --- numero ---
    @Test
    public void numeroDeveSerObrigatorio_notBlank() {
        Crm c = crmValido();
        c.setNumero("   ");

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.numero.obrigatorio}");
    }

    @Test
    public void numeroDeveRespeitarMax() {
        Crm c = crmValido();
        c.setNumero("PE" + "1".repeat(11)); // 2 letras + 11 digitos => estoura max (10 digitos)

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.numero.max}");
    }

    @Test
    public void numeroDeveRespeitarFormato() {
        Crm c = crmValido();
        c.setNumero("pe123"); // minúsculo + não bate regex

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.numero.formato}");
    }

    // --- estado ---
    @Test
    public void estadoDeveSerObrigatorio_notBlank() {
        Crm c = crmValido();
        c.setEstado("");

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.estado.obrigatorio}");
    }

    @Test
    public void estadoDeveTerFormatoDuasLetrasMaiusculas() {
        Crm c = crmValido();
        c.setEstado("P1");

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.estado.formato}");
    }

    // --- medico ---
    @Test
    public void medicoDeveSerObrigatorio_notNull() {
        Crm c = crmValido();
        c.setMedico(null);

        Set<ConstraintViolation<Crm>> v = validator.validate(c);

        assertHas(v, "{crm.medico.obrigatorio}");
    }
}
