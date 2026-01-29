/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author necta
 */
public class CrmTest {
    
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
    public void persistirCrm() {
        
        Medico medico = em.find(Medico.class, 5L);
        
        Crm crm = criarCrm("SP789102","SP", medico);
        
        em.persist(crm);
        em.flush();
        
        assertNotNull(crm.getId());
    }
    
    @Test
    public void consultarCrm() {
        Crm crm = em.find(Crm.class, 1L);
        assertEquals("SP", crm.getEstado());
        assertEquals("SP123456", crm.getNumero());
    }
    
    private Crm criarCrm(String numero, String estado, Medico medico){
        Crm crm = new Crm();
        crm.setNumero(numero);
        crm.setEstado(estado);
        crm.setMedico(medico);
        
        return crm;
    }
}
