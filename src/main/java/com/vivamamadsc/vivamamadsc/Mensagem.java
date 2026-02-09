/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import com.vivamamadsc.vivamamadsc.validation.AnexoConsistente;
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
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 *
 * @author Neto Pereira
 */
@AnexoConsistente
@Entity
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // conversa que contém a mensagem
    @NotNull(message = "{mensagem.conversa.obrigatoria}")
    @ManyToOne(optional = false)
    @JoinColumn(name = "conversa_id", nullable = false)
    private Conversa conversa;

    // remetente (pode ser Paciente ou Medico pois é Usuario)
    @NotNull(message = "{mensagem.remetente.obrigatorio}")
    @ManyToOne(optional = false)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;

    @NotBlank(message = "{mensagem.texto.obrigatorio}")
    @Size(max = 5000, message = "{mensagem.texto.max}")
    @Lob
    @Column(name = "texto", columnDefinition = "CLOB")
    private String texto;

    @NotNull(message = "{mensagem.enviadoEm.obrigatorio}")
    @jakarta.validation.constraints.PastOrPresent(message = "{mensagem.enviadoEm.passadoOuPresente}")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enviado_em", nullable = false, updatable = false)
    private Date enviadoEm;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        if (enviadoEm == null) {
            enviadoEm = new Date();
        }
    }

    @Column(name = "lida", nullable = false)
    private boolean lida = false;

    @Size(max = 255, message = "{mensagem.nomeAnexo.max}")
    @Column(name = "nome_anexo")
    private String nomeAnexo;

    /*@jakarta.validation.constraints.AssertTrue(
            message = "{mensagem.anexo.consistente}"
    )
    public boolean isAnexoConsistente() {
        boolean temAnexo = anexo != null && anexo.length > 0;
        boolean temNome = nomeAnexo != null && !nomeAnexo.isBlank();
        return temAnexo == temNome;
    }*/

    @Lob
    @Column(name = "anexo", columnDefinition = "BLOB")
    @Size(max = 5_242_880, message = "{mensagem.anexo.max}")
    private byte[] anexo;

    public Mensagem() {
    }

    public Long getId() {
        return id;
    }

    public Conversa getConversa() {
        return conversa;
    }

    public void setConversa(Conversa conversa) {
        this.conversa = conversa;
    }

    public Usuario getRemetente() {
        return remetente;
    }

    public void setRemetente(Usuario remetente) {
        this.remetente = remetente;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(Date enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public byte[] getAnexo() {
        return anexo;
    }

    public void setAnexo(byte[] anexo) {
        this.anexo = anexo;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mensagem)) {
            return false;
        }
        Mensagem other = (Mensagem) o;
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
