/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Usuario;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
public class ConversaJPQLTest {
    
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
    public void testBuscarConversaPorId() {
        Conversa c = em.createQuery(
        "SELECT c FROM Conversa c WHERE c.id = :id", Conversa.class)
                .setParameter("id", 1L)
                .getSingleResult();
        
        assertNotNull(c);
        assertEquals("Conversa inicial", c.getAssunto());
    }
    
    @Test
    public void testBuscarTodasConversas() {
        List<Conversa> c = em.createQuery(
                "SELECT c FROM Conversa c", Conversa.class)
                .getResultList();

        assertFalse(c.isEmpty());
    }
    
    @Test
    public void testBuscarConversasPorAssuntoLike() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Conversa> cq = cb.createQuery(Conversa.class);
        Root<Conversa> c = cq.from(Conversa.class);

        cq.select(c).where(
                cb.like(cb.lower(c.get("assunto")), "%inicial%")
        );

        List<Conversa> conversas = em.createQuery(cq).getResultList();

        //testem o tamanho da lista
        assertFalse(conversas.isEmpty());
        assertTrue(conversas.stream().anyMatch(x -> x.getAssunto().toLowerCase().contains("inicial")));
    }
    
    @Test
    public void testBuscarConversasPorParticipante() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Conversa> cqBase = cb.createQuery(Conversa.class);
        Root<Conversa> cBase = cqBase.from(Conversa.class);

        cBase.join("participantes");

        cqBase.select(cBase).distinct(true);

        List<Conversa> baseList = em.createQuery(cqBase)
                .setMaxResults(1)
                .getResultList();

        assertFalse("Nenhuma conversa com participante foi encontrada no dataset.", baseList.isEmpty());

        Conversa base = baseList.get(0);
        Usuario u = base.getParticipantes().get(0);

        CriteriaQuery<Conversa> cq = cb.createQuery(Conversa.class);
        Root<Conversa> c = cq.from(Conversa.class);
        Join<Conversa, Usuario> p = c.join("participantes");

        cq.select(c)
                .distinct(true)
                .where(cb.equal(p.get("id"), u.getId()));

        List<Conversa> conversasDoUsuario = em.createQuery(cq).getResultList();

        assertNotNull(conversasDoUsuario);
        assertFalse(conversasDoUsuario.isEmpty());
        assertTrue(conversasDoUsuario.stream().anyMatch(x -> x.getId().equals(base.getId())));
    }
}
