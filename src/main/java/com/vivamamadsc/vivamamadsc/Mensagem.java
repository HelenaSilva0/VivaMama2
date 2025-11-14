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
@Entity
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // conversa que contém a mensagem
    @NotNull(message = "Conversa é obrigatória")
    @ManyToOne(optional = false)
    @JoinColumn(name = "conversa_id", nullable = false)
    private Conversa conversa;

    // remetente (pode ser Paciente ou Medico pois é Usuario)
    @NotNull(message = "Remetente é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;

    @NotBlank(message = "Texto da mensagem é obrigatório")
    @Lob
    @Column(name = "texto", columnDefinition = "CLOB")
    private String texto;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enviado_em", nullable = false)
    private Date enviadoEm = new Date();

    @Column(name = "lida", nullable = false)
    private boolean lida = false;

    @Size(max = 255, message = "Nome do anexo deve ter no máximo 255 caracteres")
    @Column(name = "nome_anexo")
    private String nomeAnexo;

    @Lob
    @Column(name = "anexo", columnDefinition = "BLOB")
    private byte[] anexo;

    public Mensagem() {}

    public Long getId() { return id; }

    public Conversa getConversa() { return conversa; }
    public void setConversa(Conversa conversa) { this.conversa = conversa; }

    public Usuario getRemetente() { return remetente; }
    public void setRemetente(Usuario remetente) { this.remetente = remetente; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Date getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(Date enviadoEm) { this.enviadoEm = enviadoEm; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public String getNomeAnexo() { return nomeAnexo; }
    public void setNomeAnexo(String nomeAnexo) { this.nomeAnexo = nomeAnexo; }

    public byte[] getAnexo() { return anexo; }
    public void setAnexo(byte[] anexo) { this.anexo = anexo; }
}
