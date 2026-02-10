/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation.exame;

import com.vivamamadsc.vivamamadsc.Exame;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;

public class ExameValidator
        implements ConstraintValidator<ExameValido, Exame> {

    @Override
    public boolean isValid(Exame exame, ConstraintValidatorContext context) {

        if (exame == null) return true;

        boolean valido = true;
        context.disableDefaultConstraintViolation();

        // 1️⃣ Tipo
        if (exame.getTipo() == null) {
            context.buildConstraintViolationWithTemplate(
                    "Tipo de exame é obrigatório"
            ).addPropertyNode("tipo").addConstraintViolation();
            valido = false;
        }

        // 2️⃣ Data do exame
        LocalDate data = exame.getDataExame().toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate();

        if (data == null) {
            context.buildConstraintViolationWithTemplate(
                    "Data do exame é obrigatória"
            ).addPropertyNode("dataExame").addConstraintViolation();
            valido = false;
        } else {
            if (data.isAfter(LocalDate.now())) {
                context.buildConstraintViolationWithTemplate(
                        "{exame.data.passadoOuPresente}"
                ).addPropertyNode("dataExame").addConstraintViolation();
                valido = false;
            }
        }

        // 3️⃣ Resultado
        
        // Coerência tipo x resultado
        if (exame.getResultadoResumo()!= null &&
            !exame.getResultadoResumo().toLowerCase().contains(exame.getTipo().toLowerCase())) {

            context.buildConstraintViolationWithTemplate(
                    "{exame.incompativel.com.tipo}"
            ).addPropertyNode("resultadoResumo").addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}
