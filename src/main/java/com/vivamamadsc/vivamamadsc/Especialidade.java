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
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author helena
 */

@Entity
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @NotBlank(message = "Nome da especialidade é obrigatório")
    @Size(max = 100, message = "Nome da especialidade deve ter no máximo 100 caracteres")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

   
    @ManyToMany(mappedBy = "especialidades")
    private Set<Medico> medicos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(Set<Medico> medicos) {
        this.medicos = medicos;
    }
}
