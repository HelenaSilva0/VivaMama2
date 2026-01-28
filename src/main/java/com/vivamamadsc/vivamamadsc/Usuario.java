/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
// adicionar cpf e modificar o teste de mensagem

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{usuario.nome.obrigatorio}")
    @Size(max = 150, message = "{usuario.nome.max}")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "{usuario.cpf.obrigatorio}")
    @Pattern(regexp = "\\d{11}", message = "{usuario.cpf.formato}")
    @Column(name = "CPF", unique = true, nullable = false, length = 11)
    private String cpf;

    @NotBlank(message = "{usuario.email.obrigatorio}")
    @Email(message = "{usuario.email.invalido}")
    @Size(max = 150, message = "{usuario.email.max}")
    @Pattern(regexp = "^\\S+@\\S+\\.[^\\s]+$", message = "{usuario.email.sem_espacos}")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "{usuario.senha.obrigatoria}")
    @Size(min = 8, max = 60, message = "{usuario.senha.tamanho}")
    @Column(nullable = false, length = 60)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //@Enumerated(EnumType.STRING)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = (cpf == null) ? null : cpf.replaceAll("\\D", "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email == null) ? null : email.trim().toLowerCase();
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = (senha == null) ? null : senha.trim();
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    @jakarta.validation.constraints.AssertTrue(message = "{usuario.cpf.invalido}")
    public boolean isCpfValido() {
        if (cpf == null) {
            return false;
        }

        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return false;
        }

        boolean todosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (digits.charAt(i) != digits.charAt(0)) {
                todosIguais = false;
                break;
            }
        }
        if (todosIguais) {
            return false;
        }

        int d1 = calcularDigitoCpf(digits, 9, 10);
        int d2 = calcularDigitoCpf(digits, 10, 11);

        return d1 == Character.getNumericValue(digits.charAt(9))
                && d2 == Character.getNumericValue(digits.charAt(10));
    }

    private static int calcularDigitoCpf(String digits, int qtd, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < qtd; i++) {
            soma += Character.getNumericValue(digits.charAt(i)) * peso--;
        }
        int mod = (soma * 10) % 11;
        return (mod == 10) ? 0 : mod;
    }

@jakarta.validation.constraints.AssertTrue(message = "{usuario.senha.forte}")
    public boolean isSenhaValida() {
        if (senha == null) {
            return false;
        }

        if (senha.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$")) { 
            return true;
        }

        if (senha.length() < 8 || senha.length() > 60) {
            return false; 
        }
        if (senha.chars().anyMatch(Character::isWhitespace)) {
            return false; 
        }
        boolean temMaiuscula = false, temMinuscula = false, temNumero = false, temSimbolo = false;
        for (int i = 0; i < senha.length(); i++) {
            char c = senha.charAt(i);
            if (Character.isUpperCase(c)) {
                temMaiuscula = true;
            } else if (Character.isLowerCase(c)) {
                temMinuscula = true;
            } else if (Character.isDigit(c)) {
                temNumero = true;
            } else {
                temSimbolo = true; // qualquer coisa não letra/número vira "símbolo"
            }
        }
        return temMaiuscula && temMinuscula && temNumero && temSimbolo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
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
        final Usuario other = (Usuario) obj;
        return Objects.equals(this.id, other.id);
    }

}
