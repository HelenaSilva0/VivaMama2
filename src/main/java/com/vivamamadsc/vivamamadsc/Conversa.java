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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Neto Pereira
 */
@Entity
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Assunto é obrigatório")
    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    @Column(name = "ASSUNTO", nullable = false, length = 200)
    private String assunto;

    @NotNull(message = "Data de criação é obrigatória")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "criado_em", nullable = false)
    private Date criadoEm = new Date();

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;

    // participantes: muitos-para-muitos com Usuario
    @ManyToMany
    @JoinTable(
        name = "CONVERSA_PARTICIPANTES",
        joinColumns = @JoinColumn(name = "conversa_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> participantes = new HashSet<>();

    // construtores, getters e setters
    public Conversa() {}

    public Long getId() { return id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public Set<Usuario> getParticipantes() { return participantes; }
    public void setParticipantes(Set<Usuario> participantes) { this.participantes = participantes; }

    public void adicionarParticipante(Usuario u) { this.participantes.add(u); }
    public void removerParticipante(Usuario u) { this.participantes.remove(u); }
}
