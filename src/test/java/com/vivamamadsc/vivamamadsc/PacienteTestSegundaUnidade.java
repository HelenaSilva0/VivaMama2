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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PacienteTestSegundaUnidade {

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
    public void atualizarPaciente() {
        String novoNome = "Gameta Do Agreste";
        String novoEmail = "gameta@gmail.com";

        Paciente paciente = em.find(Paciente.class, 3L);
        paciente.setNome(novoNome);
        paciente.setEmail(novoEmail);
        em.flush();
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        paciente = em.find(Paciente.class, 3L, properties);

        assertEquals(novoNome, paciente.getNome());
        assertEquals(novoEmail, paciente.getEmail());
    }    

    @Test
    public void atualizarPacienteMerge() {
        String novoNome = "Zigoto Do Agreste";
        String novoEmail = "zigoto@gmail.com";

        Paciente paciente = em.find(Paciente.class, 3L);
        paciente.setNome(novoNome);
        paciente.setEmail(novoEmail);

        em.clear();

        paciente = (Paciente) em.merge(paciente);
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        paciente = em.find(Paciente.class, 3L, properties);

        assertEquals(novoNome, paciente.getNome());
        assertEquals(novoEmail, paciente.getEmail());
    }

    @Test
    public void removerPaciente() {
        Paciente paciente = em.find(Paciente.class, 4L);

        em.remove(paciente);
        em.flush();
        em.clear();
        Paciente pacienteRemovido = em.find(Paciente.class, 4L);
        assertNull(pacienteRemovido);
    }
}
