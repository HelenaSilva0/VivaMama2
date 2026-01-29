/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Neto
 */
@Entity
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{exame.tipo.obrigatorio}")            
    @Size(max = 100, message = "{exame.tipo.max}") 
    @Column(nullable = false, length = 100)
    private String tipo; // "mamografia", "ultrassom", "hemograma", etc

    @NotNull(message = "{exame.data.obrigatoria}")        
    @PastOrPresent(message = "{exame.data.passadoOuPresente}") 
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_EXAME", nullable = false)
    private Date dataExame;

    @Size(max = 500, message = "{exame.resultadoResumo.max}")
    @Column(name = "RESULTADO_RESUMO", length = 500)
    private String resultadoResumo; // pequeno texto com o resumo do laudo

    @NotNull(message = "{exame.paciente.obrigatorio}")
    @ManyToOne(optional = false)
    @JoinColumn(name = "PACIENTE_ID", nullable = false)
    private Paciente paciente;

    @Size(max = 5_242_880, message = "{exame.imagem.max}")
    @Lob
    @Column(name = "IMAGEM", nullable = true)
    private byte[] imagem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getDataExame() {
        return dataExame;
    }

    public void setDataExame(Date dataExame) {
        this.dataExame = dataExame;
    }

    public String getResultadoResumo() {
        return resultadoResumo;
    }

    public void setResultadoResumo(String resultadoResumo) {
        this.resultadoResumo = resultadoResumo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void addPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void removePaciente() {
        this.paciente = null;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Exame other = (Exame) obj;
        return Objects.equals(this.id, other.id);
    }
    
    
}
