/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Mensagem;
import com.vivamamadsc.vivamamadsc.Usuario;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
public class MensagemJPQLTest {

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
    public void testBuscarMensagemPorId() {
        Mensagem msg = em.createQuery(
                "SELECT m FROM Mensagem m WHERE m.id = :id", Mensagem.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(msg);
        assertEquals("Olá, seus exames chegaram. Podemos conversar?", msg.getTexto());
    }

    @Test
    public void testBuscarTodasMensagens() {
        List<Mensagem> msgs = em.createQuery(
                "SELECT m FROM Mensagem m", Mensagem.class)
                .getResultList();

        assertFalse(msgs.isEmpty());
    }

    @Test
    public void testBuscarMensagensPorTextoLike() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Mensagem> cq = cb.createQuery(Mensagem.class);
        Root<Mensagem> m = cq.from(Mensagem.class);

        cq.select(m).where(
                cb.like(cb.lower(m.get("texto")), "%exames%")
        );

        List<Mensagem> msgs = em.createQuery(cq).getResultList();

        assertNotNull(msgs);
        assertFalse(msgs.isEmpty());
        assertTrue(msgs.stream().anyMatch(x -> x.getTexto().toLowerCase().contains("exames")));
    }

    @Test
    public void testBuscarMensagensPorRemetente() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Usuario remetente = em.find(Usuario.class, 2L);

        CriteriaQuery<Mensagem> cq = cb.createQuery(Mensagem.class);
        Root<Mensagem> m = cq.from(Mensagem.class);

        cq.select(m)
                .where(cb.equal(m.get("remetente").get("id"), remetente.getId()))
                .orderBy(cb.desc(m.get("enviadoEm")));

        List<Mensagem> msgs = em.createQuery(cq).getResultList();
        //testar o tamanho da lista
        //testar a ordenação por data de envio
        assertTrue(msgs.stream().allMatch(x -> x.getRemetente().getId().equals(remetente.getId())));
    }
}
