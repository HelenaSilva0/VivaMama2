/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Medico extends Usuario {

    @NotBlank(message = "CRM é obrigatório")
    @Size(max = 20, message = "CRM deve ter no máximo 20 caracteres")
    @Column(name = "CRM", unique = true, nullable = false, length = 20)
    private String crm;

    @ManyToMany
    @JoinTable(
            name = "MEDICO_ESPECIALIDADE",
            joinColumns = @JoinColumn(name = "MEDICO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ESPECIALIDADE_ID")
    )
    private Set<Especialidade> especialidades = new HashSet<>();

    public Medico() {
        setTipo(TipoUsuario.MEDICO);
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Set<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<Especialidade> especialidades) {
        this.especialidades = especialidades;
    }

    public void adicionarEspecialidade(Especialidade esp) {
        this.especialidades.add(esp);
    }

    public void removerEspecialidade(Especialidade esp) {
        this.especialidades.remove(esp);
    }

}
