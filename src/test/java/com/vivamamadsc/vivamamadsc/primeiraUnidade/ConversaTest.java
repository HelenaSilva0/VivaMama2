/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Neto Pereira
 */
public class ConversaTest {

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
    public void testPersistenciaConversa() {
        // buscar participantes existentes (ex.: paciente id=1 e medico id=2)
        Usuario u1 = em.find(Usuario.class, 1L);
        Usuario u2 = em.find(Usuario.class, 2L);

        Conversa c = new Conversa();
        c.setAssunto("Dúvida sobre mamografia");
        if (u1 != null) {
            c.adicionarParticipante(u1);
        }
        if (u2 != null) {
            c.adicionarParticipante(u2);
        }
      
        em.persist(c);
        em.flush();

        assertNotNull("Conversa deve ter id após persistir", c.getId());
        assertTrue("Conversa deve ter pelo menos 1 participante", c.getParticipantes().size() >= 1);
        
    }

    @Test
    public void testConsultaConversaById() {
        // espera que dataset.xml contenha CONVERSA com id=1 (veja exemplo fornecido)
        Conversa conv = em.find(Conversa.class, 1L);
        assertNotNull("Conversa com id=1 deve existir (ver dataset)", conv);
        assertTrue("Conversa deve ter participantes", conv.getParticipantes().size() >= 1);
    }
}
