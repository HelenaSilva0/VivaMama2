/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Medico.PorNome",
        query = "SELECT m FROM Medico m WHERE m.nome LIKE :nome"
    )
})
public class Medico extends Usuario {

//    @NotBlank(message = "CRM é obrigatório")
//    @Size(max = 20, message = "CRM deve ter no máximo 20 caracteres")
//    @Column(name = "CRM", unique = true, nullable = false, length = 20)
    @OneToOne(mappedBy = "medico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Crm crm;

    @ManyToMany
    @JoinTable(
            name = "MEDICO_ESPECIALIDADE",
            joinColumns = @JoinColumn(name = "MEDICO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ESPECIALIDADE_ID")
    )
    private List<Especialidade> especialidades = new ArrayList<>();

    public Medico() {
        setTipo(TipoUsuario.MEDICO);
    }

    public Crm getCrm() {
        return crm;
    }

    public void setCrm(Crm crm) {
        this.crm = crm;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void addEspecialidade(Especialidade esp) {
        if (esp == null) {
            return;
        }
        if (!especialidades.contains(esp)) {
            especialidades.add(esp);
            esp.addMedico(this);
        }
    }

    public void removeEspecialidade(Especialidade esp) {
        if (esp == null) {
            return;
        }
        if (especialidades.remove(esp)) {
            esp.removeMedico(this);
        }
    }

    @Override
    public String toString() {
        return "Medico{" + "crm=" + crm.getNumero() + '}';
    }
    
    
}
