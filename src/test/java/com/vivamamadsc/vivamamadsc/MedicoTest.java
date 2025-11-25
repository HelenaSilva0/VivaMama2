/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author necta
 */
public class MedicoTest {

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
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
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
    public void testPersistenciaMedico() {
        Especialidade esp = new Especialidade();
        esp.setNome("Mastologia");
        em.persist(esp);
        
        Usuario usuario = new Usuario();
        usuario.setNome("Dr. João Lima");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao.lima@clinica.com");
        usuario.setSenha("senha123");
        usuario.setTipo(TipoUsuario.MEDICO);
        
        Medico medico = new Medico();
        
        medico.setCrm("PE12345");
        
        medico.setUsuario(usuario);
        usuario.setMedico(medico);
        
        medico.addEspecialidade(esp);

        em.persist(medico);
        em.flush();

        assertNotNull(medico.getUsuario());
        assertNotNull(medico.getUsuario().getId());
        assertEquals(usuario.getId(), medico.getUsuario().getId());
    }

    @Test
    public void testConsultaPorId() {
        Medico medico = em.find(Medico.class, 2L);
        //assertNotNull(medico);
        assertEquals("123456", medico.getCrm());
    }
}

