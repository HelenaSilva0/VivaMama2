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
 * @author Neto Pereira
 */
public class EspecialidadeTestSegundaEntrega {

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
    public void atualizarEspecialidadeSemMerge() {
        String novoNome = "Ginecologista e Obstetra Atualizado";

        Especialidade esp = em.find(Especialidade.class, 1L);
        assertNotNull("Especialidade com ID 1 deve existir", esp);
        esp.setNome(novoNome);

        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        Especialidade espAtualizada = em.find(Especialidade.class, 1L, properties);

        assertNotNull(espAtualizada);
        assertEquals(novoNome, espAtualizada.getNome());
    }

    @Test
    public void atualizarEspecialidadeComMerge() {
        String novoNome = "Especialidade Atualizada com Merge";

        Especialidade esp = em.find(Especialidade.class, 2L);
        esp.setNome(novoNome);

        em.clear();

        esp = em.merge(esp);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        Especialidade espAtualizada = em.find(Especialidade.class, 2L, properties);

        assertNotNull(espAtualizada);
        assertEquals("Nome deve ter sido atualizado via merge", novoNome, espAtualizada.getNome());
    }

    @Test
    public void removerEspecialidade() {
        Especialidade esp = em.find(Especialidade.class, 3L);
        
        assertNotNull(esp);
        
        em.remove(esp);
        em.flush();
        em.clear();
        
        Especialidade espRemovida = em.find(Especialidade.class, 3L);
        assertNull(espRemovida);
    }
}
