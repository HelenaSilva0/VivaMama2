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
public class MedicoJPQLTest {

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
    public void testBuscarMedicoPorIdJPQL() {
        Medico medico = em.createQuery(
                "SELECT m FROM Medico m WHERE m.id = :id", Medico.class)
                .setParameter("id", 2L)
                .getSingleResult();

        assertNotNull(medico);
        assertEquals("Fulano da Silva", medico.getNome());
    }

    @Test
    public void testBuscarTodosMedicosJPQL() {
        List<Medico> medicos = em.createQuery(
                "SELECT m FROM Medico m", Medico.class
        ).getResultList();

        assertFalse(medicos.isEmpty());
    }

    // TESTES FRACOS E ANTIGOS COM CRITERIA
    @Test
    public void deveBuscarMedicoPorNomeParcial() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Medico> cq = cb.createQuery(Medico.class);
        Root<Medico> medico = cq.from(Medico.class);

        Predicate nome = cb.like(medico.get("nome"), "%Fulano%");
        cq.where(nome);

        List<Medico> resultado = em.createQuery(cq).getResultList();
        assertFalse(resultado.isEmpty());

        em.close();
    }

    @Test
    public void deveBuscarMedicosQuePossuemCrm() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Medico> cq = cb.createQuery(Medico.class);
        Root<Medico> medico = cq.from(Medico.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Crm> crm = subquery.from(Crm.class);

        subquery.select(cb.literal(1L))
                .where(cb.equal(crm.get("medico"), medico));

        cq.select(medico)
                .where(cb.exists(subquery));

        List<Medico> medicos = em.createQuery(cq).getResultList();
        assertFalse(medicos.isEmpty());

        em.close();
    }

    // TESTE NOVO JPQL (subquery COUNT) + teste de lista (especialidades)
    @Test
    public void deveRetornarApenasMedicosComMaisDeUmaEspecialidade_JPQL() {
        List<Medico> medicos = em.createQuery(
                """
                SELECT m
                FROM Medico m
                WHERE (
                    SELECT COUNT(e)
                    FROM Medico m2 JOIN m2.especialidades e
                    WHERE m2 = m
                ) > 1
                ORDER BY (
                    SELECT COUNT(e)
                    FROM Medico m2 JOIN m2.especialidades e
                    WHERE m2 = m
                ) DESC
                """,
                Medico.class
        ).getResultList();

        assertFalse(medicos.isEmpty());

        for (Medico medico : medicos) {
            Long qtd = em.createQuery(
                    """
                    SELECT COUNT(e)
                    FROM Medico m2 JOIN m2.especialidades e
                    WHERE m2 = :medico
                    """,
                    Long.class
            )
                    .setParameter("medico", medico)
                    .getSingleResult();

            assertTrue("Médico deveria ter mais de uma especialidade", qtd > 1);

            assertNotNull(medico.getEspecialidades());
            assertFalse(medico.getEspecialidades().isEmpty());
        }
    }

    // TESTE NOVO CRITERIA (EXISTS subquery) + teste de lista (especialidades)
    @Test
    public void deveBuscarMedicosQuePossuemEspecialidades_Criteria() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Medico> cq = cb.createQuery(Medico.class);
        Root<Medico> medico = cq.from(Medico.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Medico> m2 = subquery.from(Medico.class);
        m2.join("especialidades");

        subquery.select(cb.literal(1L))
                .where(cb.equal(m2.get("id"), medico.get("id")));

        cq.select(medico)
                .where(cb.exists(subquery));

        List<Medico> medicos = em.createQuery(cq).getResultList();
        assertFalse(medicos.isEmpty());

        for (Medico m : medicos) {
            assertNotNull(m.getEspecialidades());
            assertFalse(m.getEspecialidades().isEmpty());
        }

        em.close();
    }
}
