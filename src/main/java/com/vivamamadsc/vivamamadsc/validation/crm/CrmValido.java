package com.vivamamadsc.vivamamadsc.validation.crm;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CrmValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrmValido {
    String message() default "{crm.invalido}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
