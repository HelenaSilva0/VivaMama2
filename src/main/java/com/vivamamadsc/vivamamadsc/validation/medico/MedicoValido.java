package com.vivamamadsc.vivamamadsc.validation.medico;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 *
 * @author emilly
 */

@Documented
@Constraint(validatedBy = MedicoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MedicoValido {
    String message() default "{medico.invalido}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
