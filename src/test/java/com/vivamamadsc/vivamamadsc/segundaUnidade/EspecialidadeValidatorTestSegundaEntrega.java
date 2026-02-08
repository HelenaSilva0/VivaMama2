/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Especialidade;
import com.vivamamadsc.vivamamadsc.base.BaseTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
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

/**
 *
 * @author Neto Pereira
 */
public class EspecialidadeValidatorTestSegundaEntrega extends BaseTest {

    @Test(expected = ConstraintViolationException.class)
    public void persistirEspecialidadeInvalido() {
        Especialidade especialidade = null;

        try {
            especialidade = new Especialidade();

            especialidade.setNome("");

            em.persist(especialidade);
            em.flush();
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

            violations.forEach(v -> {
                assertThat(
                        v.getRootBeanClass() + "." + v.getPropertyPath() + ": " + v.getMessage(),
                        CoreMatchers.anyOf(
                                startsWith("class com.vivamamadsc.vivamamadsc.Especialidade.nome")
                        )
                );
            });

            assertEquals(1, violations.size());
            assertNull(especialidade.getId());

            Map<String, List<String>> mensagensPorCampo = new HashMap<>();

            violations.forEach(v -> {
                mensagensPorCampo
                        .computeIfAbsent(
                                v.getPropertyPath().toString(),
                                k -> new ArrayList<>()
                        )
                        .add(v.getMessage());
            });

            assertTrue(mensagensPorCampo.get("nome")
                    .contains("{especialidade.nome.obrigatorio}"));

            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void atualizarEspecialidadeInvalido() {
        Especialidade esp = em.createQuery(
                "SELECT e FROM Especialidade e", Especialidade.class
        ).setMaxResults(1).getSingleResult();

        try {
            String novoNomeEspecialidade = "A".repeat(1000);
            esp.setNome(novoNomeEspecialidade);

            em.flush();
        } catch (ConstraintViolationException ex) {
            ConstraintViolation<?> violation = ex
                    .getConstraintViolations()
                    .iterator()
                    .next();
            
            assertEquals("nome", violation.getPropertyPath().toString());
            assertEquals("{especialidade.nome.max}", violation.getMessage());
            
            assertEquals(1, ex.getConstraintViolations().size());

            throw ex;
        }
    }
}
