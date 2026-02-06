package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Paciente;
import com.vivamamadsc.vivamamadsc.base.BaseTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class PacienteValidatorTestSegundaEntrega extends BaseTest {

    @Test(expected = ConstraintViolationException.class)
    public void persistirPacienteInvalido() {
        Paciente paciente = null;
        Calendar calendar = new GregorianCalendar();

        try {
            paciente = new Paciente();
            paciente.setNome(""); // inválido (NotBlank)
            paciente.setEmail("email_invalido@"); // inválido (@Pattern & @Email)
            paciente.setCpf("123"); // CPF inválido (@Pattern & @AssertTrue isCpfValido)
            paciente.setSenha("123"); // Senha inválido (@Pattern & @AssertTrue isSenhaValida)

            calendar.set(2030, Calendar.JANUARY, 1);
            paciente.setDataNascimento(calendar.getTime()); // data futura inválida (@Past)

            String textoGrande = "A".repeat(9001);
            paciente.setHistoricoFamiliar(textoGrande); // texto ultrapassa o limite máximo

            byte[] pdfGrande = new byte[9_242_880];
            paciente.setHistoricoFamiliarPdf(pdfGrande); // texto ultrapassa o limite máximo

            em.persist(paciente);
            em.flush();
        } catch (ConstraintViolationException ex) {

            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

            violations.forEach(v -> {
                assertThat(
                        v.getRootBeanClass() + "." + v.getPropertyPath() + ": " + v.getMessage(),
                        CoreMatchers.anyOf(
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.nome"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.email"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.cpf"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.senha"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.dataNascimento"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.historicoFamiliar"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Paciente.historicoFamiliarPdf")
                        )
                );
            });

            assertEquals(10, violations.size()); // ajuste se necessário
            assertNull(paciente.getId());

            Map<String, List<String>> mensagensPorCampo = new HashMap<>();

            ex.getConstraintViolations().forEach(v -> {
                mensagensPorCampo
                        .computeIfAbsent(
                                v.getPropertyPath().toString(),
                                k -> new ArrayList<>()
                        )
                        .add(v.getMessage());
            });

            assertEquals(10, ex.getConstraintViolations().size());

            assertTrue(mensagensPorCampo.get("nome")
                    .contains("{usuario.nome.obrigatorio}"));

            assertTrue(mensagensPorCampo.get("email")
                    .contains("{usuario.email.invalido}"));

            assertTrue(mensagensPorCampo.get("cpf")
                    .contains("{usuario.cpf.formato}"));

            assertTrue(mensagensPorCampo.get("senha")
                    .contains("{usuario.senha.tamanho}"));

            assertTrue(mensagensPorCampo.get("dataNascimento")
                    .contains("{paciente.dataNascimento.passada}"));

            assertTrue(mensagensPorCampo.get("historicoFamiliar")
                    .contains("{paciente.historicoTexto.max}"));

            assertTrue(mensagensPorCampo.get("historicoFamiliarPdf")
                    .contains("{paciente.historicoPdf.max}"));

            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void atualizarPacienteInvalido() {

        Paciente paciente = em.createQuery(
                "SELECT p FROM Paciente p WHERE p.cpf = :cpf", Paciente.class
        ).setParameter("cpf", "05980165037")
                .getSingleResult();

        try {
            paciente.setSenha("123"); // Senha inválido (@Pattern & @AssertTrue isSenhaValida)

            em.flush();
        } catch (ConstraintViolationException ex) {

            ConstraintViolation<?> violation = ex
                    .getConstraintViolations()
                    .iterator()
                    .next();

            assertEquals("{usuario.senha.tamanho}", violation.getMessage());

            assertEquals(2, ex.getConstraintViolations().size());

            throw ex;
        }
    }
}
