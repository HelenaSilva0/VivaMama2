/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Especialidade;
import com.vivamamadsc.vivamamadsc.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author helena
 */
public class EspecialidadeTest {
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
    public void testPersistenciaEspecialidade() {
        Especialidade esp = new Especialidade();
        esp.setNome("Oncologista");

        em.persist(esp);
        em.flush(); 

        assertNotNull(esp.getId());
        assertEquals("Oncologista", esp.getNome());
    }
    
    @Test
    public void testConsultaEspecialidadePorId() {
        Especialidade esp = em.find(Especialidade.class, 2L); 

        assertNotNull(esp);
        assertEquals("mastologista", esp.getNome());
    }
    
    @Test
    public void testMedicosDaEspecialidade() {
        Especialidade esp = em.find(Especialidade.class, 2L); 
        assertNotNull(esp);

        // Carrega a lista de médicos associados
        assertNotNull(esp.getMedicos());
        assertTrue(!esp.getMedicos().isEmpty());

        Medico medico = esp.getMedicos().get(0);
        assertNotNull(medico);
        assertEquals("SP123456", medico.getCrm().getNumero());
    }
    
}
