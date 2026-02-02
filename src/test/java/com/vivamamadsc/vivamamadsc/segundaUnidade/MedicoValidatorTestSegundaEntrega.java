package com.vivamamadsc.vivamamadsc.segundaUnidade;
import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.Especialidade;
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
 * @author Emilly Maria
 */
public class MedicoValidatorTestSegundaEntrega {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("pt", "BR"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Medico medicoValido() {
        Medico m = new Medico();

        // CRM válido (e amarra o "volta" pq Crm exige medico != null)
        Crm c = new Crm();
        c.setEstado("PE");
        c.setNumero("PE123"); // seu regex exige 2 letras + 1..10 dígitos
        c.setMedico(m);

        m.setCrm(c);

        // Especialidades: Medico só exige min=1 e item != null
        m.getEspecialidades().add(new Especialidade());

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

    // --- testes CRM no Medico ---
    @Test
    public void crmDeveSerObrigatorio_notNull() {
        Medico m = medicoValido();
        m.setCrm(null);

        Set<ConstraintViolation<Medico>> v = validator.validate(m);

        assertHas(v, "{medico.crm.obrigatorio}");
    }

    // --- testes ESPECIALIDADES ---
    @Test
    public void deveTerPeloMenosUmaEspecialidade() {
        Medico m = medicoValido();
        m.getEspecialidades().clear();

        Set<ConstraintViolation<Medico>> v = validator.validate(m);

        assertHas(v, "{medico.especialidades.min}");
    }

    @Test
    public void especialidadesNaoPodemConterNull() {
        Medico m = medicoValido();
        m.getEspecialidades().add(null);

        Set<ConstraintViolation<Medico>> v = validator.validate(m);

        assertHas(v, "{medico.especialidades.item.obrigatorio}");
    }
}
