/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Exame;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
public class ExameTestSegundaEntrega {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("vivamamadsc");
        // carrega dataset.xml no banco
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
        } else {
            et.rollback();
        }
        em.close();
    }

    @Test
    public void atualizarExameSemMerge() {
        Exame exame = em.find(Exame.class, 1L);
        assertNotNull(exame);

        String novoTipo = "Mamografia Digital";
        String novoResultado = "Resultado atualizado sem uso do merge.";

        exame.setTipo(novoTipo);
        exame.setResultadoResumo(novoResultado);

        em.flush();

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        exame = em.find(Exame.class, 1L, properties);

        assertEquals(novoTipo, exame.getTipo());
        assertEquals("Resultado deve ter sido atualizado", novoResultado, exame.getResultadoResumo());

        assertNotNull("Relacionamento com paciente deve ser mantido", exame.getPaciente());
        assertEquals("ID do paciente deve permanecer o mesmo", Long.valueOf(1L), exame.getPaciente().getId());
    }

    @Test
    public void atualizarExameComMerge() {
        Exame exame = em.find(Exame.class, 2L);
        assertNotNull(exame);

        String novoTipo = exame.getTipo() + " [ATUALIZADO COM MERGE]";
        String novoResultado = "Resultado atualizado com uso do merge.";

        exame.setTipo(novoTipo);
        exame.setResultadoResumo(novoResultado);

        em.clear();
        
        exame = em.merge(exame);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);

        Exame exameAtualizado = em.find(Exame.class, 2L, properties);

        assertNotNull(exameAtualizado);
        assertEquals(novoTipo, exameAtualizado.getTipo());
        assertEquals(novoResultado, exameAtualizado.getResultadoResumo());
    }

    @Test
    public void removerExame() {
        Exame exame = em.find(Exame.class, 3L);
        em.remove(exame);
        em.flush();
        em.clear();
        
        Exame exameRemovido = em.find(Exame.class, 3L);
        assertNull(exameRemovido);
    }
}
