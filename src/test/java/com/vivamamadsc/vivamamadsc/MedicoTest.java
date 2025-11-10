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
        Medico medico = new Medico();
        medico.setNome("Dr. João Lima");
        medico.setEmail("joao.lima@clinica.com");
        medico.setSenha("senha123");
        medico.setCrm("PE12345");
        medico.setEspecialidade("Pediatria");
        //medico.setTelefone("(81) 98888-7777");
        //medico.setHorarioAtendimento("Seg a Sex - 8h às 17h");

        em.persist(medico);
        em.flush();

        assertNotNull(medico.getId());
    }

    @Test
    public void testConsultaPorId() {
        Medico medico = em.find(Medico.class, 2L);
//        assertNotNull(medico.getId());
        assertEquals("1234456", medico.getCrm());
    }
}

