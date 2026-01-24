/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Crm;
import com.vivamamadsc.vivamamadsc.Medico;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

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
 * @author Emilly Maria
 */
public class CrmJPQLTest {

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

    // TESTES ANTIGOS E SIMPLES COM JPQL
    @Test
    public void testBuscarCrmPorIdJPQL() {
        Crm crm = em.createQuery(
                "SELECT c FROM Crm c WHERE c.id = :id", Crm.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(crm);
        assertEquals("SP123456", crm.getNumero());
        assertEquals("SP", crm.getEstado());
    }

    @Test
    public void testBuscarTodosCrmsJPQL() {
        List<Crm> crms = em.createQuery(
                "SELECT c FROM Crm c", Crm.class
        ).getResultList();

        assertFalse(crms.isEmpty());
    }

    // TESTES FRACOS E ANTIGOS COM CRITERIA
    @Test
    public void deveBuscarCrmPorNumeroParcial_Criteria() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Crm> cq = cb.createQuery(Crm.class);
        Root<Crm> crm = cq.from(Crm.class);

        Predicate numero = cb.like(crm.get("numero"), "%123%");
        cq.where(numero);

        List<Crm> resultado = em.createQuery(cq).getResultList();
        assertFalse(resultado.isEmpty());

        em.close();
    }

    @Test
    public void deveBuscarCrmsPorEstado_Criteria() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Crm> cq = cb.createQuery(Crm.class);
        Root<Crm> crm = cq.from(Crm.class);

        Predicate estado = cb.equal(crm.get("estado"), "SP");
        cq.where(estado);

        List<Crm> resultado = em.createQuery(cq).getResultList();
        assertFalse(resultado.isEmpty());

        em.close();
    }

    // TESTE NOVO JPQL (subquery COUNT) + valida médico associado
    @Test
    public void deveRetornarApenasCrmsComMedico_JPQL() {
        List<Crm> crms = em.createQuery(
                """
                SELECT c
                FROM Crm c
                WHERE (
                    SELECT COUNT(m)
                    FROM Medico m
                    WHERE m = c.medico
                ) > 0
                ORDER BY c.estado ASC, c.numero ASC
                """,
                Crm.class
        ).getResultList();

        assertFalse(crms.isEmpty());

        for (Crm c : crms) {
            assertNotNull(c.getMedico());
        }
    }

    // TESTE NOVO CRITERIA (EXISTS subquery) + valida médico não nulo
    @Test
    public void deveBuscarCrmsComMedico_Criteria() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Crm> cq = cb.createQuery(Crm.class);
        Root<Crm> crm = cq.from(Crm.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Medico> medico = subquery.from(Medico.class);

        subquery.select(cb.literal(1L))
                .where(cb.equal(medico.get("id"), crm.get("medico").get("id")));

        cq.select(crm)
                .where(cb.exists(subquery));

        List<Crm> crms = em.createQuery(cq).getResultList();
        assertFalse(crms.isEmpty());

        for (Crm c : crms) {
            assertNotNull(c.getMedico());
        }

        em.close();
    }
}