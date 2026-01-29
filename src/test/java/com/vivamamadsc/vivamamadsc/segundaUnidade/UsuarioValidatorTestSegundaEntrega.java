/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.TipoUsuario;
import com.vivamamadsc.vivamamadsc.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Helena
 */
public class UsuarioValidatorTestSegundaEntrega {

    private static Validator validator;

    private static class UsuarioTeste extends Usuario {
        
    }

    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("pt", "BR"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    private UsuarioTeste usuarioValido() {
        UsuarioTeste u = new UsuarioTeste();
        u.setNome("Fulano da Silva");
        u.setCpf("52998224725");                 
        u.setEmail("fulano@gmail.com");         
        u.setSenha("Abcdef1!");                  
        TipoUsuario[] valores = TipoUsuario.values();
        if (valores.length > 0) {
            u.setTipo(valores[0]);
        }
        return u;
    }

    private Set<String> templates(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.toSet());
    }
    
      private Set<String> paths(Set<? extends ConstraintViolation<?>> violations) { // MODIFICADO: novo helper (estilo "Vendedor")
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    private void assertHas(Set<? extends ConstraintViolation<?>> violations, String template) {
        Set<String> t = templates(violations);
        assertTrue("Esperava violação com template: " + template + " mas veio: " + t, t.contains(template));
    }
    
    private void assertHasPath(Set<? extends ConstraintViolation<?>> violations, String path) { // MODIFICADO: novo helper
        Set<String> p = paths(violations);
        assertTrue("Esperava violação no campo: " + path + " mas veio: " + p, p.contains(path));
    }
    
    //teste user invalido
    @Test
    public void usuarioInvalido() {
        UsuarioTeste u = usuarioValido();
        u.setNome("   ");
        u.setCpf("123");
        u.setEmail("abc");
        u.setSenha("Ab1!"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHasPath(v, "nome");
        assertHasPath(v, "cpf");
        assertHasPath(v, "email");
        assertHasPath(v, "senha");

        assertHas(v, "{usuario.nome.obrigatorio}");
        assertHas(v, "{usuario.cpf.formato}");
        assertHas(v, "{usuario.cpf.invalido}");
        assertHas(v, "{usuario.email.invalido}");
        assertHas(v, "{usuario.email.sem_espacos}");
        assertHas(v, "{usuario.senha.tamanho}");
        assertHas(v, "{usuario.senha.forte}");
    }


    // teste NOME

    @Test
    public void nomeDeveSerNotBlank() {
        UsuarioTeste u = usuarioValido();
        u.setNome("   "); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.nome.obrigatorio}");
    }

    @Test
    public void nomeDeveRespeitarMax150() {
        UsuarioTeste u = usuarioValido();
        u.setNome("a".repeat(151)); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.nome.max}");
    }

    //teste CPF

    @Test
    public void cpfDeveSerNotBlankEValido() {
        UsuarioTeste u = usuarioValido();
        u.setCpf(null);

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.cpf.obrigatorio}");
        assertHas(v, "{usuario.cpf.invalido}"); 
    }

    @Test
    public void cpfDeveTer11Digitos() {
        UsuarioTeste u = usuarioValido();
        u.setCpf("123"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.cpf.formato}");
        assertHas(v, "{usuario.cpf.invalido}");
    }

    @Test
    public void cpfDevePassarAlgoritmoDigitosRepetidos() {
        UsuarioTeste u = usuarioValido();
        u.setCpf("11111111111"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.cpf.invalido}");
    }

    @Test
    public void cpfDeveAceitarFormatoComPontosEHifen() {
        UsuarioTeste u = usuarioValido();
        u.setCpf("529.982.247-25"); // setter remove não-dígitos

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertTrue("CPF formatado deveria ficar válido após normalização. Violações: " + templates(v), v.isEmpty());
        assertEquals("52998224725", u.getCpf());
    }

    // teste EMAIL

    @Test
    public void emailDeveSerNotBlank() {
        UsuarioTeste u = usuarioValido();
        u.setEmail("   ");

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.email.obrigatorio}");
        assertHas(v, "{usuario.email.sem_espacos}");
        
    }

    @Test
    public void emailDeveTerFormatoValido() {
        UsuarioTeste u = usuarioValido();
        u.setEmail("abc"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.email.invalido}");
        assertHas(v, "{usuario.email.sem_espacos}");
    }

    @Test
    public void emailNaoPodeTerEspacos() {
        UsuarioTeste u = usuarioValido();
        u.setEmail("fulano @gmail.com"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.email.invalido}");
        assertHas(v, "{usuario.email.sem_espacos}");
    }

    @Test
    public void emailDeveRespeitarMax150() {
        UsuarioTeste u = usuarioValido();

    String emailLongo =
            "a@" +
            "b".repeat(63) + "." +
            "c".repeat(63) + "." +
            "d".repeat(17) + ".com"; 

    assertEquals(151, emailLongo.length());

    u.setEmail(emailLongo);

    Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

    assertHas(v, "{usuario.email.max}");
    }

    @Test
    public void emailDeveSerNormalizadoELowercase() {
        UsuarioTeste u = usuarioValido();
        u.setEmail("  FULANO@GMAIL.COM  ");

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertTrue("E-mail deveria estar válido após normalização. Violações: "
                + templates(v), v.isEmpty());
        assertEquals("fulano@gmail.com", u.getEmail());
    }

    //teste SENHA

    @Test
    public void senhaDeveSerNotBlank() {
        UsuarioTeste u = usuarioValido();
        u.setSenha("   "); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.senha.obrigatoria}");
        assertHas(v, "{usuario.senha.tamanho}");
        assertHas(v, "{usuario.senha.forte}");
    }

    @Test
    public void senhaDeveRespeitarTamanhoMin8() {
        UsuarioTeste u = usuarioValido();
        u.setSenha("Ab1!"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.senha.tamanho}");
        assertHas(v, "{usuario.senha.forte}");
    }

    @Test
    public void senhaDeveConterMaiusNinusNumSimbolo() {
        UsuarioTeste u = usuarioValido();
        u.setSenha("abcdefgh");

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.senha.forte}");
    }

    @Test
    public void senhaNaoPodeTerEspaco() {
        UsuarioTeste u = usuarioValido();
        u.setSenha("Abcd ef1!"); 

        Set<ConstraintViolation<UsuarioTeste>> v = validator.validate(u);

        assertHas(v, "{usuario.senha.forte}");
    }
    

}
