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
public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {
        if (senha == null) {
            return true; // deixa @NotBlank resolver
        }
        // se for hash bcrypt, aceita
        if (senha.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$")) {
            return true;
        }

        if (senha.length() < 8 || senha.length() > 60) {
            return false;
        }
        if (senha.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }

        boolean temMaiuscula = false, temMinuscula = false, temNumero = false, temSimbolo = false;
        for (int i = 0; i < senha.length(); i++) {
            char c = senha.charAt(i);
            if (Character.isUpperCase(c)) {
                temMaiuscula = true;
            } else if (Character.isLowerCase(c)) {
                temMinuscula = true;
            } else if (Character.isDigit(c)) {
                temNumero = true;
            } else {
                temSimbolo = true;
            }
        }
        return temMaiuscula && temMinuscula && temNumero && temSimbolo;
    }
}
