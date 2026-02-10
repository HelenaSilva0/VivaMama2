package com.vivamamadsc.vivamamadsc.validation.crm;

import com.vivamamadsc.vivamamadsc.Crm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CrmValidator implements ConstraintValidator<CrmValido, Crm> {

    @Override
    public boolean isValid(Crm crm, ConstraintValidatorContext context) {
        if (crm == null) return true;

        boolean ok = true;
        context.disableDefaultConstraintViolation();

        // numero obrigatório
        String numero = crm.getNumero();
        if (numero == null || numero.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("{crm.numero.obrigatorio}")
                    .addPropertyNode("numero")
                    .addConstraintViolation();
            ok = false;
        } else {
            String n = numero.trim();

            // max 10
            if (n.length() > 10) {
                context.buildConstraintViolationWithTemplate("{crm.numero.max}")
                        .addPropertyNode("numero")
                        .addConstraintViolation();
                ok = false;
            }

            // formato (alinha com o que teu teste já viu: {crm.numero.formato})
            if (!n.matches("^[0-9]{1,10}$")) {
                context.buildConstraintViolationWithTemplate("{crm.numero.formato}")
                        .addPropertyNode("numero")
                        .addConstraintViolation();
                ok = false;
            }
        }

        // estado obrigatório + formato UF
        String uf = crm.getEstado();
        if (uf == null || uf.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("{crm.estado.obrigatorio}")
                    .addPropertyNode("estado")
                    .addConstraintViolation();
            ok = false;
        } else if (!uf.matches("^[A-Z]{2}$")) {
            context.buildConstraintViolationWithTemplate("{crm.estado.formato}")
                    .addPropertyNode("estado")
                    .addConstraintViolation();
            ok = false;
        }

        // medico obrigatório
        if (crm.getMedico() == null) {
            context.buildConstraintViolationWithTemplate("{crm.medico.obrigatorio}")
                    .addPropertyNode("medico")
                    .addConstraintViolation();
            ok = false;
        }

        return ok;
    }
}
