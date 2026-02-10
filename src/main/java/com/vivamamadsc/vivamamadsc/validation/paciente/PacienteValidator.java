package com.vivamamadsc.vivamamadsc.validation.paciente;

import com.vivamamadsc.vivamamadsc.Paciente;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class PacienteValidator implements ConstraintValidator<PacienteValido, Paciente> {

    @Override
    public boolean isValid(Paciente paciente, ConstraintValidatorContext context) {

        if (paciente == null) return true;

        boolean valido = true;
        context.disableDefaultConstraintViolation();

        LocalDate nascimento = paciente.getDataNascimento().toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate();

        // 1️⃣ Data de nascimento
        if (nascimento == null) {
            context.buildConstraintViolationWithTemplate(
                    "Data de nascimento é obrigatória"
            ).addPropertyNode("dataNascimento").addConstraintViolation();
            valido = false;
        } else {
            if (nascimento.isAfter(LocalDate.now())) {
                context.buildConstraintViolationWithTemplate(
                        "Data de nascimento não pode ser futura"
                ).addPropertyNode("dataNascimento").addConstraintViolation();
                valido = false;
            }

            if (nascimento.isBefore(LocalDate.now().minusYears(130))) {
                context.buildConstraintViolationWithTemplate(
                        "{paciente.idade.invalida.maior.de.centoETrinta}"
                ).addPropertyNode("dataNascimento").addConstraintViolation();
                valido = false;
            }
        }

        // 2️⃣ Histórico familiar
        int idade = Period.between(paciente.getDataNascimento().toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate(), LocalDate.now()).getYears();

        if (idade >= 18 && paciente.getHistoricoFamiliar() == null) {
            context.buildConstraintViolationWithTemplate(
                    "Histórico familiar deve ser informado para pacientes adultos"
            ).addPropertyNode("historicoFamiliar").addConstraintViolation();
            valido = false;
        }

        // Exemplo de coerência clínica
        if (idade < 12 && paciente.getHistoricoFamiliar() != null &&
            paciente.getHistoricoFamiliar().toLowerCase().contains("câncer")) {

            context.buildConstraintViolationWithTemplate(
                    "{paciente.historicoFamiliar.com.incoerencia.clinica}"
            ).addPropertyNode("historicoFamiliar").addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}