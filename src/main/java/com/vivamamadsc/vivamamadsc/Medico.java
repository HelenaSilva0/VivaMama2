/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Medico extends Usuario {
 
    @NotBlank(message = "CRM é obrigatório")
    @Size(max = 20, message = "CRM deve ter no máximo 20 caracteres")
    @Column(name = "CRM", unique = true, nullable = false, length = 20)
    private String crm;

    // Separar o atributo especialidade numa classe para criar o relacionamento many to many
    @NotBlank(message = "Especialidade é obrigatória")
    @Size(max = 100, message = "Especialidade deve ter no máximo 100 caracteres")
    @Column(name = "ESPECIALIDADE", nullable = false, length = 100)
    private String especialidade;

    public Medico() {
        setTipo(TipoUsuario.MEDICO);
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

}
