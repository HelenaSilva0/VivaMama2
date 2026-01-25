/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Especialidade;
import com.vivamamadsc.vivamamadsc.Medico;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
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
public class EspecialidadeJPQLTest {

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
    public void testBuscarEspecialidadePorIdJPQL() {
        Especialidade esp = em.createQuery(
                "SELECT e FROM Especialidade e WHERE e.id = :id", Especialidade.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(esp);
        assertEquals("ginecologista", esp.getNome());
    }

    @Test
    public void testBuscarTodasEspecialidades() {
        List<Especialidade> especialidades = em.createQuery(
                "SELECT e FROM Especialidade e", Especialidade.class
        ).getResultList();

        assertFalse(especialidades.isEmpty());
    }

    // TESTES NOVO COM JPQL E CRITERIA
    // JPQL  
    @Test
    public void testRelatorioEspecialidadesPorEstadoCrmJPQL() {
        String jpql = "SELECT e.nome, m.nome, c.estado "
                + "FROM Especialidade e "
                + "JOIN e.medicos m "
                + "JOIN m.crm c "
                + "WHERE c.estado = :uf "
                + "ORDER BY e.nome ASC";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("uf", "SP");

        List<Object[]> resultados = query.getResultList();

        assertFalse("A lista de resultados não deve estar vazia", resultados.isEmpty());

        assertEquals("Deve retornar exatamente 2 especialidades associadas ao médico de SP", 2, resultados.size());

        Object[] primeiroResultado = resultados.get(0);

        assertEquals("ginecologista", primeiroResultado[0]);
        assertEquals("Fulano da Silva", primeiroResultado[1]);
        assertEquals("SP", primeiroResultado[2]);

        Object[] segundoResultado = resultados.get(1);
        assertEquals("mastologista", segundoResultado[0]);
        assertEquals("SP", segundoResultado[2]);
    }

    @Test
    public void testContagemMedicosPorEspecialidadeCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Especialidade> especialidade = cq.from(Especialidade.class);
        Join<Especialidade, Medico> medico = especialidade.join("medicos"); // Inner join

        cq.multiselect(especialidade.get("nome"), cb.count(medico));

        cq.groupBy(especialidade.get("nome"));

        cq.orderBy(cb.desc(cb.count(medico)), cb.asc(especialidade.get("nome")));

        TypedQuery<Object[]> query = em.createQuery(cq);
        List<Object[]> resultados = query.getResultList();

        assertFalse("A lista de contagem não deve estar vazia", resultados.isEmpty());

        assertEquals(2, resultados.size());

        for (Object[] resultado : resultados) {
            String nomeEspecialidade = (String) resultado[0];
            Long quantidade = (Long) resultado[1];

            assertNotNull("Nome da especialidade não pode ser nulo", nomeEspecialidade);
            assertTrue("Quantidade de médicos deve ser maior que 0", quantidade > 0);

            if ("ginecologista".equals(nomeEspecialidade)) {
                assertEquals(Long.valueOf(1), quantidade);
            } else if ("mastologista".equals(nomeEspecialidade)) {
                assertEquals(Long.valueOf(1), quantidade);
            }
        }
    }
}
