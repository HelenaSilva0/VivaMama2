/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Usuario;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Helena
 */
public class ParticipantesValidosValidator implements ConstraintValidator<ParticipantesValidos, Conversa> {

    @Override
    public boolean isValid(Conversa c, ConstraintValidatorContext context) {
        if (c == null) {
            return true;
        }

        List<Usuario> participantes = c.getParticipantes();
        if (participantes == null) {
            return true; 
        }
        // pelo menos 2 participantes
        if (participantes.size() < 2) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{conversa.participantes.min2}") // <-- NOVA MSG
                    .addPropertyNode("participantes")
                    .addConstraintViolation();
            return false;
        }

        // nao pode ter participantes repetidos
        Set<Usuario> set = new HashSet<>();
        for (Usuario u : participantes) {
            if (u != null && !set.add(u)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{conversa.participantes.duplicados}") // <-- NOVA MSG
                        .addPropertyNode("participantes")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
