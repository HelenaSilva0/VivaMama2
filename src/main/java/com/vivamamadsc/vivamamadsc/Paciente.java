/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Paciente.PorNome",
        query = "SELECT p FROM Paciente p WHERE p.nome LIKE :nome"
    )
})
public class Paciente extends Usuario {

    @Past(message = "{paciente.dataNascimento.passada}") 
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_NASCIMENTO", nullable = true)
    private Date dataNascimento;

    @Size(max = 5_242_880, message = "{paciente.historicoPdf.max}")
    @Lob
    @Column(name = "HISTORICO_FAMILIAR_PDF")
    private byte[] historicoFamiliarPdf;

    @Size(max = 5000, message = "{paciente.historicoTexto.max}")
    @Lob
    @Column(name = "HISTORICOFAMILIAR")
    private String historicoFamiliar;

    @OneToMany(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Exame> exames = new ArrayList<>();

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

    public List<Exame> getExames() {
        return exames;
    }

    public void addExames(Exame exame) {
        if (exame == null) {
            return;
        }
        exames.add(exame);
        exame.addPaciente(this);
    }

    public void removeExame(Exame exame) {
        exames.remove(exame);
        exame.removePaciente();
    }

}
