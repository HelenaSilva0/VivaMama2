/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PACIENTE")
public class Paciente {
    
    @Id
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_NASCIMENTO", nullable = true)
    private Date dataNascimento;

    @Lob
    @Column(name = "HISTORICO_FAMILIAR_PDF")
    private byte[] historicoFamiliarPdf;

    @Lob
    @Column(name = "HISTORICOFAMILIAR")
    private String historicoFamiliar;

    @OneToMany(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Exame> exames = new ArrayList<>();

    public Paciente() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public byte[] getHistoricoFamiliarPdf() {
        return historicoFamiliarPdf;
    }

    public void setHistoricoFamiliarPdf(byte[] historicoFamiliarPdf) {
        this.historicoFamiliarPdf = historicoFamiliarPdf;
    }

    public String getHistoricoFamiliar() {
        return historicoFamiliar;
    }

    public void setHistoricoFamiliar(String historicoFamiliar) {
        this.historicoFamiliar = historicoFamiliar;
    }

    public List<Exame> getExames() {
        return exames;
    }

    public void addExames(Exame exame) {
        if (exame == null)
            return;
        exames.add(exame);
        exame.addPaciente(this);
    }

    public void removeExame(Exame exame) {
        exames.remove(exame);
        exame.removePaciente();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paciente)) {
            return false;
        }
        Paciente other = (Paciente) o;
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

}
