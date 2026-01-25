/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Conversa;
import com.vivamamadsc.vivamamadsc.Mensagem;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
//testes simples

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

    //teste com criteria
    
    @Test
    public void testBuscarConversasPorAssuntoLike() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Conversa> cq = cb.createQuery(Conversa.class);
        Root<Conversa> c = cq.from(Conversa.class);

        cq.select(c).where(cb.like(cb.lower(c.get("assunto")), "%inicial%"));

        List<Conversa> conversas = em.createQuery(cq).getResultList();

        assertNotNull(conversas);
        assertEquals(3, conversas.size());
        assertTrue(conversas.stream().allMatch(x
                -> x.getAssunto() != null && x.getAssunto().toLowerCase().contains("inicial")
        ));
    }

    @Test
    public void testBuscarConversasPorParticipante() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Usuario u = em.find(Usuario.class, 1L);
        assertNotNull("Usuário 1 não encontrado no dataset.", u);

        CriteriaQuery<Conversa> cq = cb.createQuery(Conversa.class);
        Root<Conversa> c = cq.from(Conversa.class);
        Join<Conversa, Usuario> p = c.join("participantes");

        cq.select(c)
                .distinct(true)
                .where(cb.equal(p.get("id"), u.getId()));

        List<Conversa> conversasDoUsuario = em.createQuery(cq).getResultList();

        assertNotNull(conversasDoUsuario);
        assertEquals(2, conversasDoUsuario.size());
        assertTrue(conversasDoUsuario.stream().allMatch(conv
                -> conv.getParticipantes().stream().anyMatch(part -> part.getId().equals(u.getId()))
        ));
    }
    
    //teste com JPQL puro

    @Test
    public void testeJpqlAssuntosConversasCriadasPorData() {
        Date inicio = Timestamp.valueOf("2025-08-01 00:00:00");
        Date fim = Timestamp.valueOf("2025-08-11 00:00:00");

        List<Conversa> conversas = em.createQuery(
                "SELECT DISTINCT c "
                + "FROM Conversa c "
                + "JOIN c.participantes p "
                + "WHERE c.criadoEm >= :inicio AND c.criadoEm < :fim "
                + "ORDER BY c.criadoEm ASC",
                Conversa.class
        )
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        List<String> assuntos = conversas.stream()
                .map(Conversa::getAssunto)
                .collect(Collectors.toList());

        assertNotNull(assuntos);
        assertEquals(5, assuntos.size());

        assertEquals(Arrays.asList(
                "Resultado de mamografia",
                "Resultado de biópsia",
                "Discussão de diagnóstico",
                "Ressonância das mamas",
                "Mamografia de rotina"
        ), assuntos);
    }



    @Test
    public void testJpqlConversaMensagensERemetentes() {
        Long conversaId = 4L;

        em.clear();

        List<Conversa> result = em.createQuery(
                "SELECT c "
                + "FROM Conversa c "
                + "JOIN FETCH c.mensagens m "
                + "JOIN FETCH m.remetente r "
                + "WHERE c.id = :cid",
                Conversa.class
        )
                .setParameter("cid", conversaId)
                .getResultList();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        Conversa conversa = result.get(0);

        assertNotNull(conversa);
        assertEquals(conversaId, conversa.getId());
        assertEquals("Resultado de mamografia", conversa.getAssunto());

        List<Mensagem> msgs = conversa.getMensagens();
        assertNotNull(msgs);

        long qtdMensagensDistintas = msgs.stream()
                .map(Mensagem::getId)
                .distinct()
                .count();
        assertEquals(5L, qtdMensagensDistintas);

        Set<Long> remetentes = msgs.stream()
                .map(m -> m.getRemetente().getId())
                .collect(Collectors.toSet());
        assertEquals(new HashSet<>(Arrays.asList(18L, 8L)), remetentes);

        assertTrue(msgs.stream().anyMatch(m
                -> "Apenas manter acompanhamento anual.".equals(m.getTexto())
        ));
    }

}
