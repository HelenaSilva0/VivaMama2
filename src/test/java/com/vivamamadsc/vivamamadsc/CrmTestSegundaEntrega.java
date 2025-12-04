/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Emilly
 */
public class CrmTestSegundaEntrega {

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
    public void atualizarCrm() {
        Crm obj = em.find(Crm.class, 3L);
        obj.setNumero("RS999999");
        obj.setEstado("RS");
        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Crm.class, 3L, properties);

        assertEquals("RS999999", obj.getNumero());
        assertEquals("RS", obj.getEstado());
    }

    @Test
    public void atualizarCrmMerge() {
        Crm obj = em.find(Crm.class, 1L);
        obj.setNumero("RS888888");
        obj.setEstado("RS");

        em.clear();
        obj = em.merge(obj);

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Crm.class, 1L, properties);

        assertEquals("RS888888", obj.getNumero());
        assertEquals("RS", obj.getEstado());
    }

    @Test
    public void removerCrm() {

        Crm crm = em.find(Crm.class, 2L);
        // quebrar relacionamento com médico
        Medico medico = crm.getMedico();
        if (medico != null) {
            medico.setCrm(null);
        }

        em.remove(crm);
        em.flush();
        em.clear();

        Crm objRemovido = em.find(Crm.class, 2L);
        assertNull(objRemovido);
    }

}
