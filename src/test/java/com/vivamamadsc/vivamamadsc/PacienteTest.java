/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Calendar;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class PacienteTest {
    
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private EntityTransaction et;
    
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("vivamamadsc");
        DbUnitUtil.inserirDados();
    }
    
    @AfterClass
    public static void tearDownClass() {
        emf.close();
    }
    
    @Before
    public void setUp() {
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @After
    public void tearDown() {
        if (!et.getRollbackOnly()) {
            et.commit();
        }
        em.close();
    }
    
    @Test
    public void persistirUsuario() {
        Paciente usuario;
        usuario = criarUsuario();
        em.persist(usuario);
        em.flush(); //forÃ§a que a persistÃªncia realizada vÃ¡ para o banco neste momento.

        assertNotNull(usuario.getId());
    }
    
    @Test
    public void consultarUsuario() {
        Paciente paciente = em.find(Paciente.class, 1L);
        assertEquals("ciclano@gmail.com", paciente.getEmail());
        assertEquals("Ciclano da Silva", paciente.getNome());
    }

    private Paciente criarUsuario() {
        Paciente paciente = new Paciente();
        paciente.setNome("Beltrano da Silva");
        paciente.setCpf("12345678777");
        paciente.setEmail("beltrano@gmail.com");
        paciente.setSenha("teste");
        paciente.setTipo(TipoUsuario.PACIENTE);
        paciente.setHistoricoFamiliar("Sem histórico familiar.");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1981);
        c.set(Calendar.MONTH, Calendar.FEBRUARY);
        c.set(Calendar.DAY_OF_MONTH, 25);
        paciente.setDataNascimento(c.getTime());

        return paciente;
    }
    
}
