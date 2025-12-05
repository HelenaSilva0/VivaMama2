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
 * @author Emilly
 */
public class MedicoTestSegundaEntrega {
    
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
    public void atualizarMedico() {
        String novoNome = "Lua Santana";
        String novoEmail = "luanetes@gmail.com";

        Medico medico = em.find(Medico.class, 5L);
        medico.setNome(novoNome);
        medico.setEmail(novoEmail);

        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        medico = em.find(Medico.class, 5L, properties);

        assertEquals(novoNome, medico.getNome());
        assertEquals(novoEmail, medico.getEmail());
    }

    @Test
    public void atualizarMedicoMerge() {
        
        String novoNome = "Joelma Vieira";
        String novoEmail = "tacaca@gmail.com";

        Medico medico = em.find(Medico.class, 2L);
        medico.setNome(novoNome);
        medico.setEmail(novoEmail);

        em.clear();

        medico = (Medico) em.merge(medico);

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        medico = em.find(Medico.class, 2L, properties);

        assertEquals(novoNome, medico.getNome());
        assertEquals(novoEmail, medico.getEmail());
        
    }

    @Test
    public void removerMedico() {
        Medico obj = em.find(Medico.class, 6L);
        
        em.remove(obj);
        em.flush();
        em.clear();
        Medico objRemovido = em.find(Medico.class, 6L);
        assertNull(objRemovido);
    }
    
}
