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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "MEDICO")
public class Medico {
    
    @Id
    private Long id;
            
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;

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
    private List<Especialidade> especialidades = new ArrayList<>();

    public Medico() {
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

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void addEspecialidade(Especialidade esp) {
         if (esp == null) return;
        if (!especialidades.contains(esp)) {
            especialidades.add(esp);
            esp.addMedico(this);
        }
    }
    
     public void removeEspecialidade(Especialidade esp) {
        if (esp == null) return;
        if (especialidades.remove(esp)) {
            esp.removeMedico(this);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medico)) {
            return false;
        }
        Medico other = (Medico) o;
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
