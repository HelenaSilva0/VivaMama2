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
import static junit.framework.Assert.assertNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author helena
 */


public class ConversaTestSegundaEntrega {
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
    public void atualizarConversa() {
        Conversa obj = em.find(Conversa.class, 1L);
        obj.setAssunto("Assunto Atualizado");
        obj.setAtiva(false);
        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Conversa.class, 1L, properties);

        assertEquals("Assunto Atualizado", obj.getAssunto());
        assertEquals(false, obj.isAtiva());
    }

    @Test
    public void atualizarConversaMerge() {
        Conversa obj = em.find(Conversa.class, 3L);
        obj.setAssunto("Assunto Atualizado Com Merge");
        obj.setAtiva(false);

        em.clear();
        obj = em.merge(obj);

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Conversa.class, 3L, properties);

        assertEquals("Assunto Atualizado Com Merge", obj.getAssunto());
        assertEquals(false, obj.isAtiva());
    }

    @Test
    public void removerConversa() {
        Conversa obj = em.find(Conversa.class, 2L);
        
        em.remove(obj);
        em.flush();
        em.clear();
        Conversa objRemovido = em.find(Conversa.class, 2L);
        assertNull(objRemovido);
    }
}

