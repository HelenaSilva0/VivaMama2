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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.Date;
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
 * @author Helena
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

    //testes simples
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

    //testes com Criteria
    
    @Test
    public void testBuscarMensagensPorTextoLike() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Mensagem> cq = cb.createQuery(Mensagem.class);
        Root<Mensagem> m = cq.from(Mensagem.class);

        Predicate filtro = cb.like(cb.lower(m.get("texto")), "%exames%");

        cq.select(m)
                .where(filtro)
                .orderBy(
                        cb.desc(m.get("enviadoEm")),
                        cb.desc(m.get("id"))
                );

        List<Mensagem> msgs = em.createQuery(cq).getResultList();

        assertNotNull(msgs);

        assertEquals(5, msgs.size());

        assertTrue(msgs.stream().allMatch(x
                -> x.getTexto() != null && x.getTexto().toLowerCase().contains("exames")
        ));

        for (int i = 1; i < msgs.size(); i++) {
            Mensagem anterior = msgs.get(i - 1);
            Mensagem atual = msgs.get(i);

            int cmpData = anterior.getEnviadoEm().compareTo(atual.getEnviadoEm());

            boolean ok
                    = (cmpData > 0)
                    || (cmpData == 0 && anterior.getId() >= atual.getId()); // mesma data, id maior primeiro

            assertTrue(
                    "Lista fora de ordem em i=" + i
                    + " (anterior=" + anterior.getEnviadoEm() + ", id=" + anterior.getId()
                    + " / atual=" + atual.getEnviadoEm() + ", id=" + atual.getId() + ")",
                    ok
            );
        }
    }

    @Test
    public void testBuscarMensagensPorRemetente() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Usuario remetente = em.find(Usuario.class, 2L);

        CriteriaQuery<Mensagem> cq = cb.createQuery(Mensagem.class);
        Root<Mensagem> m = cq.from(Mensagem.class);

        cq.select(m)
                .where(cb.equal(m.get("remetente").get("id"), remetente.getId()))
                .orderBy(
                        cb.desc(m.get("enviadoEm")),
                        cb.desc(m.get("id"))
                );

        List<Mensagem> msgs = em.createQuery(cq).getResultList();

        assertNotNull(msgs);
        assertEquals(2, msgs.size());

        assertTrue(msgs.stream().allMatch(x
                -> x.getRemetente() != null && x.getRemetente().getId().equals(remetente.getId())
        ));
        for (int i = 1; i < msgs.size(); i++) {
            Mensagem anterior = msgs.get(i - 1);
            Mensagem atual = msgs.get(i);

            int cmpData = anterior.getEnviadoEm().compareTo(atual.getEnviadoEm());

            boolean ok
                    = (cmpData > 0)
                    || (cmpData == 0 && anterior.getId() >= atual.getId());

            assertTrue("Lista fora de ordem na posição " + i, ok);
        }
    }

    //teste com JPQL puro
    
    @Test
    public void testJpqlBuscarNaoLidasPorConversa() {
        Long conversaId = 4L;

        List<Mensagem> msgs = em.createQuery(
                "SELECT m "
                + "FROM Mensagem m "
                + "WHERE m.conversa.id = :cid AND m.lida = false "
                + "ORDER BY m.enviadoEm ASC, m.id ASC",
                Mensagem.class
        )
                .setParameter("cid", conversaId)
                .getResultList();

        assertNotNull(msgs);
        assertEquals(5, msgs.size()); 

        assertTrue(msgs.stream().allMatch(m
                -> m.getConversa().getId().equals(conversaId) && !m.isLida()
        ));

        for (int i = 1; i < msgs.size(); i++) {
            Mensagem ant = msgs.get(i - 1);
            Mensagem atual = msgs.get(i);

            int cmp = ant.getEnviadoEm().compareTo(atual.getEnviadoEm());
            boolean ok = (cmp < 0) || (cmp == 0 && ant.getId() <= atual.getId());
            assertTrue("Lista fora de ordem na posição " + i, ok);
        }
    }

    @Test
    public void testJpqlBuscarMensagensEntreDatas() {
        Date inicio = Timestamp.valueOf("2025-12-02 00:00:00");
        Date fim = Timestamp.valueOf("2025-12-03 00:00:00"); 

        List<Mensagem> msgs = em.createQuery(
                "SELECT m "
                + "FROM Mensagem m "
                + "WHERE m.enviadoEm >= :inicio AND m.enviadoEm < :fim "
                + "ORDER BY m.enviadoEm ASC",
                Mensagem.class
        )
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        assertNotNull(msgs);
        assertEquals(3, msgs.size()); 

        assertTrue(msgs.stream().allMatch(m
                -> !m.getEnviadoEm().before(inicio) && m.getEnviadoEm().before(fim)
        ));

        for (int i = 1; i < msgs.size(); i++) {
            Mensagem ant = msgs.get(i - 1);
            Mensagem atual = msgs.get(i);

            assertFalse("Lista fora de ordem na posição " + i,
                    ant.getEnviadoEm().after(atual.getEnviadoEm())
            );
        }
    }

}
