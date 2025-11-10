/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Medico extends Usuario {// colocar o descriminator

    // Colocar o tamanho das strings
    @Column(unique = true, nullable = false)
    private String crm;

    // Separar o atributo especialidade numa classe para criar o relacionamento many to many
    @Column(name = "ESPECIALIDADE")
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
