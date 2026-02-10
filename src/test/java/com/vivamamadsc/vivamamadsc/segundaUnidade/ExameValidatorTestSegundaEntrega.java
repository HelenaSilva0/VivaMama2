/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Exame;
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

/**
 *
 * @author Neto Pereira
 */
public class ExameValidatorTestSegundaEntrega extends BaseTest {

    @Test(expected = ConstraintViolationException.class)
    public void persistirExameInvalido() {

        Exame exame = null;
        Calendar calendar = new GregorianCalendar();

        try {
            exame = new Exame();

            exame.setTipo(""); // @NotBlank

            calendar.set(2030, Calendar.JANUARY, 1);
            exame.setDataExame(calendar.getTime()); // futura -> inválida

            exame.setPaciente(null); // @NotNull

            String resumoGrande = "A".repeat(1000);
            exame.setResultadoResumo(resumoGrande); // > 500

            byte[] imagemGrande = new byte[6_000_000];
            exame.setImagem(imagemGrande); // > 5_242_880

            em.persist(exame);
            em.flush();

        } catch (ConstraintViolationException ex) {

            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

            violations.forEach(v -> {
                assertThat(
                        v.getRootBeanClass() + "." + v.getPropertyPath() + ": " + v.getMessage(),
                        CoreMatchers.anyOf(
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.tipo"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.dataExame"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.paciente"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.resultadoResumo"),
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.imagem")
                        )
                );
            });

            assertEquals(6, violations.size());
            assertNull(exame.getId());

            Map<String, List<String>> mensagensPorCampo = new HashMap<>();

            violations.forEach(v -> {
                mensagensPorCampo
                        .computeIfAbsent(
                                v.getPropertyPath().toString(),
                                k -> new ArrayList<>()
                        )
                        .add(v.getMessage());
            });

            assertTrue(mensagensPorCampo.get("tipo")
                    .contains("{exame.tipo.obrigatorio}"));

            assertTrue(mensagensPorCampo.get("dataExame")
                    .contains("{exame.data.passadoOuPresente}"));

            assertTrue(mensagensPorCampo.get("paciente")
                    .contains("{exame.paciente.obrigatorio}"));
            
            assertTrue(mensagensPorCampo.get("resultadoResumo")
                    .contains("{exame.resultadoResumo.max}"));

            assertTrue(mensagensPorCampo.get("imagem")
                    .contains("{exame.imagem.max}"));

            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void atualizarExameInvalido() {

        Exame exame = em.createQuery(
                "SELECT e FROM Exame e", Exame.class
        ).setMaxResults(1)
                .getSingleResult();

        try {
            String resumoGrande = "A".repeat(1000);
            exame.setResultadoResumo(resumoGrande); // > 500

            em.flush();

        } catch (ConstraintViolationException ex) {

            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

            violations.forEach(v -> {
                assertThat(
                        v.getRootBeanClass() + "." + v.getPropertyPath() + ": " + v.getMessage(),
                        CoreMatchers.anyOf(
                                startsWith("class com.vivamamadsc.vivamamadsc.Exame.resultadoResumo")
                        )
                );
            });

            assertEquals(2, violations.size());

            Map<String, List<String>> mensagensPorCampo = new HashMap<>();

            violations.forEach(v -> {
                mensagensPorCampo
                        .computeIfAbsent(
                                v.getPropertyPath().toString(),
                                k -> new ArrayList<>()
                        )
                        .add(v.getMessage());
            });

            assertTrue(mensagensPorCampo.get("resultadoResumo")
                    .contains("{exame.incompativel.com.tipo}"));
            
            assertTrue(mensagensPorCampo.get("resultadoResumo")
                    .contains("{exame.resultadoResumo.max}"));

            mensagensPorCampo.forEach((campo, mensagens) -> {
                System.out.println("Campo: " + campo);
                mensagens.forEach(msg -> System.out.println("  - " + msg));
            });

            throw ex;
        }
    }
}
