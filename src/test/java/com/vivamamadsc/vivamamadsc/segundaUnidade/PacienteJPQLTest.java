/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Exame;
import com.vivamamadsc.vivamamadsc.Paciente;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Date;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Neto Pereira
 */
public class PacienteJPQLTest {

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

    // TESTES ANTIGOS E SIMPLES COM JQPL
    @Test
    public void testBuscarPacientePorIdJPQL() {
        Paciente paciente = em.createQuery(
                "SELECT m FROM Paciente m WHERE m.id = :id", Paciente.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(paciente);
        assertEquals("Maria da Silva", paciente.getNome());
    }

    @Test
    public void testBuscarTodosPacientes() {
        List<Paciente> pacientes = em.createQuery(
                "SELECT p FROM Paciente p", Paciente.class
        ).getResultList();

        assertFalse(pacientes.isEmpty());

    }

    // TESTES FRACOS E ANTIGOS COM CRITERIA
    @Test
    public void deveBuscarPacientePorNomeParcialECpf() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Paciente> cq = cb.createQuery(Paciente.class);
        Root<Paciente> paciente = cq.from(Paciente.class);

        Predicate nome = (Predicate) cb.like(paciente.get("nome"), "%Miriapode%");
        Predicate cpf = (Predicate) cb.equal(paciente.get("cpf"), "87772267045");

        cq.where(cb.and(nome, cpf));

        List<Paciente> resultado = em.createQuery(cq).getResultList();

        //assertFalse(resultado.isEmpty());
        assertEquals(resultado.size(), 1);

    }

    @Test
    public void deveBuscarPacientesQuePossuemExames() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Paciente> cq = cb.createQuery(Paciente.class);
        Root<Paciente> paciente = cq.from(Paciente.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Exame> exame = subquery.from(Exame.class);

        subquery.select(cb.literal(1L))
                .where(cb.equal(exame.get("paciente"), paciente));

        cq.select(paciente)
                .where(cb.exists(subquery));

        List<Paciente> pacientes = em.createQuery(cq).getResultList();

        assertFalse(pacientes.isEmpty());

    }

    // TESTES NOVO COM JPQL E CRITERIA
    // JPQL
    @Test
    public void deveRetornarApenasPacientesComMaisDeUmExame() {
        List<Paciente> pacientes = em.createQuery(
                """
                SELECT p
                FROM Paciente p
                WHERE (
                    SELECT COUNT(e)
                    FROM Exame e
                    WHERE e.paciente = p
                ) > 1
                ORDER BY (
                    SELECT MAX(e.dataExame)
                    FROM Exame e
                    WHERE e.paciente = p
                ) DESC
                """,
                Paciente.class
        ).getResultList();

        assertFalse(pacientes.size() < 0);

        for (Paciente paciente : pacientes) {
            Long quantidadeExames = em.createQuery(
                    """
            SELECT COUNT(e)
            FROM Exame e
            WHERE e.paciente = :paciente
            """,
                    Long.class
            )
                    .setParameter("paciente", paciente)
                    .getSingleResult();

            assertTrue("Paciente " + paciente.getNome() + " deveria ter mais de um exame", quantidadeExames > 1);
        }
    }

    public @Test
    void deveOrdenarPorDataDoExameMaisRecenteDesc() {
        List<Paciente> pacientes = em.createQuery(
                """
                SELECT p
                FROM Paciente p
                WHERE (
                    SELECT COUNT(e)
                    FROM Exame e
                    WHERE e.paciente = p
                ) > 1
                ORDER BY (
                    SELECT MAX(e.dataExame)
                    FROM Exame e
                    WHERE e.paciente = p
                ) DESC
                """,
                Paciente.class
        ).getResultList();

        assertTrue(pacientes.size() >= 2);

        Paciente primeiro = pacientes.get(0);
        Paciente segundo = pacientes.get(1);

        Date maxPrimeiro = dataMaisRecente(primeiro);
        Date maxSegundo = dataMaisRecente(segundo);

        assertTrue("Paciente com exame mais recente deve vir primeiro",
                maxPrimeiro.after(maxSegundo));
    }

    // CRITERIA
    @Test
    public void deveRetornarIdsDePacientesComMaisDeUmExameCriteria() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Paciente> paciente = cq.from(Paciente.class);
        Join<Paciente, Exame> exameJoin = paciente.join("exames");

        cq.select(paciente.get("id"))
                .groupBy(paciente.get("id"))
                .having(cb.gt(cb.count(exameJoin), 1L));

        List<Long> idsPacientes = em.createQuery(cq).getResultList();

        // ---------- ASSERTS ----------
        assertTrue(idsPacientes.size() > 0);

        for (Long id : idsPacientes) {
            Long totalExames = em.createQuery(
                    "SELECT COUNT(e) FROM Exame e WHERE e.paciente.id = :id",
                    Long.class
            ).setParameter("id", id)
                    .getSingleResult();

            assertTrue(totalExames > 1);
        }
    }

    private Date dataMaisRecente(Paciente paciente) {
        return paciente.getExames().stream()
                .map(Exame::getDataExame)
                .max(Date::compareTo)
                .orElseThrow();
    }

}
