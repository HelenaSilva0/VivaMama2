/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *
 * @author Helena
 */
public class PrimeiraLetraMaiusculaValidator implements ConstraintValidator<PrimeiraLetraMaiuscula, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
      
        
        if (valor == null || valor.isBlank()) {
            return true;
        }

       
        return Character.isUpperCase(valor.charAt(0));
    }
}
