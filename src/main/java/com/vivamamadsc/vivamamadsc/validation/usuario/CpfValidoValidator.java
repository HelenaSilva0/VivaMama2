/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation.usuario;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *
 * @author Helena
 */
public class CpfValidoValidator implements ConstraintValidator<CpfValido, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null) {
            return true; // deixa @NotBlank cuidar do null/vazio
        }
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return false;
        }

        boolean todosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (digits.charAt(i) != digits.charAt(0)) {
                todosIguais = false;
                break;
            }
        }
        if (todosIguais) {
            return false;
        }

        int d1 = calcularDigitoCpf(digits, 9, 10);
        int d2 = calcularDigitoCpf(digits, 10, 11);

        return d1 == Character.getNumericValue(digits.charAt(9))
                && d2 == Character.getNumericValue(digits.charAt(10));
    }

    private static int calcularDigitoCpf(String digits, int qtd, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < qtd; i++) {
            soma += Character.getNumericValue(digits.charAt(i)) * peso--;
        }
        int mod = (soma * 10) % 11;
        return (mod == 10) ? 0 : mod;
    }
}
