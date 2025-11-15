/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Paciente extends Usuario {

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
    private Set<Exame> exames = new HashSet<>();

    public Paciente() {
        setTipo(TipoUsuario.PACIENTE);
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

    public Set<Exame> getExames() {
        return exames;
    }

    public void setExames(Set<Exame> exames) {
        this.exames = exames;
    }

    public void adicionarExame(Exame exame) {
        exames.add(exame);
        exame.setPaciente(this);
    }

    public void removerExame(Exame exame) {
        exames.remove(exame);
        exame.setPaciente(null);
    }

}
