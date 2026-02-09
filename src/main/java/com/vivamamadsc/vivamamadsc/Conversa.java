/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import com.vivamamadsc.vivamamadsc.validation.ParticipantesValidos;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Neto Pereira
 */
@ParticipantesValidos
@Entity
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{conversa.assunto.obrigatorio}")
    @Size(max = 200, message = "{conversa.assunto.max}")   
    @Column(name = "ASSUNTO", nullable = false, length = 200)
    private String assunto;

    @NotNull(message = "{conversa.criadoEm.obrigatorio}")
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
            inverseJoinColumns = @JoinColumn(name = "usuario_id"),
    uniqueConstraints = @UniqueConstraint(columnNames = {"conversa_id", "usuario_id"})
)
            
    @Size(min = 1, message = "{conversa.participantes.min}") 
    private List<@NotNull(message = "{conversa.participantes.item.obrigatorio}") Usuario> participantes = new ArrayList<>();
    
    @OneToMany(mappedBy = "conversa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensagem> mensagens = new ArrayList<>();

    // construtores, getters e setters
    public Conversa() {
    }

    public Long getId() {
        return id;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void adicionarParticipante(Usuario u) {
         if (u == null) throw new IllegalArgumentException("Participante não pode ser null");
    if (!this.participantes.contains(u)) this.participantes.add(u);
}

    public void removerParticipante(Usuario u) {
        if (u == null) return;
    this.participantes.remove(u);
}
    
    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final Conversa other = (Conversa) obj;
        return Objects.equals(this.id, other.id);
    }

}
