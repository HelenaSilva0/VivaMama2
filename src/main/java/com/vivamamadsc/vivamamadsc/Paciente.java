/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;


@Entity
public class Paciente extends Usuario {
    
@Temporal(TemporalType.DATE)
@Column(name = "DT_NASCIMENTO", nullable = true)
    private Date dataNascimento;
    
@Lob//arquivoPDF - blob
@Column(name = "HISTORICOFAMILIAR")
    private String historicoFamiliar;


    public Paciente() {
        setTipo(TipoUsuario.PACIENTE);
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getHistoricoFamiliar() {
        return historicoFamiliar;
    }

    public void setHistoricoFamiliar(String historicoFamiliar) {
        this.historicoFamiliar = historicoFamiliar;
    }
    
}
