/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Mensagem;
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
public class MensagemTestSegundaEntrega {

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
    public void atualizarMensagem() {
        Mensagem obj = em.find(Mensagem.class, 3L);
        obj.setTexto("Mensagem atualizada.");
        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Mensagem.class, 3L, properties);

        assertEquals("Mensagem atualizada.", obj.getTexto());
    }

    @Test
    public void atualizarMensagemMerge() {
        Mensagem obj = em.find(Mensagem.class, 1L);
        obj.setTexto("Mensagem atualizada com Merge.");

        em.clear();
        obj = em.merge(obj);

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        obj = em.find(Mensagem.class, 1L, properties);

        assertEquals("Mensagem atualizada com Merge.", obj.getTexto());
    }

    @Test
    public void removerMensagem() {
        Mensagem obj = em.find(Mensagem.class, 2L);
        em.remove(obj);
        em.flush();
        em.clear();
        Mensagem objRemovido = em.find(Mensagem.class, 2L);
        assertNull(objRemovido);
    }
}
