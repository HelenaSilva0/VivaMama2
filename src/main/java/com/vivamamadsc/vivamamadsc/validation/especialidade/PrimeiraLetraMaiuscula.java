/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.validation.especialidade;

import com.vivamamadsc.vivamamadsc.validation.especialidade.PrimeiraLetraMaiusculaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Helena
 */
@Documented
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrimeiraLetraMaiusculaValidator.class)
public @interface PrimeiraLetraMaiuscula {

    String message() default "{com.vivamamadsc.vivamamadsc.validation.PrimeiraLetraMaiuscula.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
