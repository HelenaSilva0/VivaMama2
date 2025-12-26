/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Neto Pereira
 */
public class CrmJPQLTest {
    
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
    public void testBuscarCrmPorId(){
        Crm crm = em.createQuery(
        "SELECT c FROM Crm c WHERE c.id = :id", Crm.class)
                .setParameter("id", 1L)
                .getSingleResult();
        
        assertNotNull(crm);
        assertEquals("SP123456", crm.getNumero());
        assertEquals("SP", crm.getEstado());
    }
    
    @Test
    public void testBuscarTodosCrms(){
        List<Crm> crms = em.createQuery(
        "SELECT c FROM Crm c", Crm.class)
                .getResultList();
        
        assertFalse(crms.isEmpty());
    }
}
