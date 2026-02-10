package com.vivamamadsc.vivamamadsc.validation.medico;

import com.vivamamadsc.vivamamadsc.Especialidade;
import com.vivamamadsc.vivamamadsc.Medico;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class MedicoValidator implements ConstraintValidator<MedicoValido, Medico> {

    @Override
    public boolean isValid(Medico medico, ConstraintValidatorContext context) {
        if (medico == null) return true;

        boolean ok = true;
        context.disableDefaultConstraintViolation();

        // CRM obrigatório
        if (medico.getCrm() == null) {
            context.buildConstraintViolationWithTemplate("{medico.crm.obrigatorio}")
                    .addPropertyNode("crm")
                    .addConstraintViolation();
            ok = false;
        }

        // Especialidades: pelo menos 1 (não nula)
        List<Especialidade> list = medico.getEspecialidades();

        int validas = 0;
        if (list != null) {
            for (Especialidade e : list) {
                if (e != null) validas++;
            }
        }

        if (validas < 1) {
            context.buildConstraintViolationWithTemplate("{medico.especialidades.min}")
                    .addPropertyNode("especialidades")
                    .addConstraintViolation();
            ok = false;
        }

        // Item não pode ser nulo
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    context.buildConstraintViolationWithTemplate("{medico.especialidades.item.obrigatorio}")
                            .addPropertyNode("especialidades")
                            .inIterable().atIndex(i)
                            .addConstraintViolation();
                    ok = false;
                }
            }
        }

        return ok;
    }
}
