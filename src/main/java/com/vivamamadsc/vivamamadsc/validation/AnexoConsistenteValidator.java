/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation;

import com.vivamamadsc.vivamamadsc.Mensagem;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *
 * @author Helena
 */

public class AnexoConsistenteValidator implements ConstraintValidator<AnexoConsistente, Mensagem> {

    @Override
    public boolean isValid(Mensagem m, ConstraintValidatorContext context) {
        if (m == null) return true;

        boolean temAnexo = m.getAnexo() != null && m.getAnexo().length > 0;
        boolean temNome = m.getNomeAnexo() != null && !m.getNomeAnexo().isBlank();

        if (temAnexo == temNome) return true;

        context.disableDefaultConstraintViolation();
        if (temAnexo && !temNome) {
            context.buildConstraintViolationWithTemplate("{mensagem.nomeAnexo.obrigatorioQuandoAnexo}")
                   .addPropertyNode("nomeAnexo")
                   .addConstraintViolation();
        } else {
            context.buildConstraintViolationWithTemplate("{mensagem.anexo.obrigatorioQuandoNome}")
                   .addPropertyNode("anexo")
                   .addConstraintViolation();
        }
        return false;
    }
}

