/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 *
 * @author neto
 */

@Entity
public class Crm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{crm.numero.obrigatorio}")
    @Size(max = 12, message = "{crm.numero.max}")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{1,10}$",
        message = "{crm.numero.formato}"            // MODIFICADO
    )
    @Size(max = 10, message = "{crm.numero.max}")
    private String numero;
    
    @NotBlank(message = "{crm.estado.obrigatorio}") 
    @Pattern(regexp = "^[A-Z]{2}$", message = "{crm.estado.formato}")
    private String estado;

    @NotNull(message = "{crm.medico.obrigatorio}")
    @OneToOne
    @JoinColumn(name = "medico_id", unique = true)
    private Medico medico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final Crm other = (Crm) obj;
        return Objects.equals(this.id, other.id);
    }
}
