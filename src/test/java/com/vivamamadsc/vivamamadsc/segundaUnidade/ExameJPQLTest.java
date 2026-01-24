/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Exame;
import com.vivamamadsc.vivamamadsc.Paciente;
import com.vivamamadsc.vivamamadsc.Usuario;
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
import java.time.LocalDate;
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
 * @author Neto Pereira
 */
public class ExameJPQLTest {

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
    public void testBuscarExamePorId() {
        Exame ex = em.createQuery(
                "SELECT e FROM Exame e WHERE e.id = :id", Exame.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(ex);
        assertEquals("Mamografia", ex.getTipo());
    }

    @Test
    public void testBuscarExamesPorPaciente() {
        List<Exame> exames = em.createQuery(
                "SELECT e FROM Exame e WHERE e.paciente.id = :idPaciente", Exame.class
        )
                .setParameter("idPaciente", 1L)
                .getResultList();

        assertFalse(exames.isEmpty());
    }

    @Test
    public void deveBuscarExamesPorTipoEPeriodo() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Exame> cq = cb.createQuery(Exame.class);
        Root<Exame> exame = cq.from(Exame.class);

        Predicate tipo = cb.equal(exame.get("tipo"), "Sangue");

        Predicate periodo = cb.between(
                exame.get("dataExame"),
                java.sql.Date.valueOf(LocalDate.of(2025, 1, 1)),
                java.sql.Date.valueOf(LocalDate.of(2025, 12, 31))
        );

        cq.where(cb.and(tipo, periodo));

        List<Exame> exames = em.createQuery(cq).getResultList();

        assertNotNull(exames); //não está testando...

        em.close();
    }

    @Test
    public void deveBuscarExamesPorPaciente() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Exame> cq = cb.createQuery(Exame.class);
        Root<Exame> exame = cq.from(Exame.class);

        Join<Object, Object> paciente = exame.join("paciente");

        cq.where(cb.equal(paciente.get("id"), 1L));

        List<Exame> exames = em.createQuery(cq).getResultList();

        //testar o tamanho da lista
        assertFalse(exames.isEmpty());

        assertTrue(exames.size() > 0);

        em.close();
    }

    @Test
    public void deveContarExamesDeUmPaciente() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Exame> exame = cq.from(Exame.class);

        Join<Object, Object> paciente = exame.join("paciente");

        cq.select(cb.count(exame))
                .where(cb.equal(paciente.get("id"), 1L));

        Long total = em.createQuery(cq).getSingleResult();

        assertTrue(total >= 0);

        em.close();
    }

    @Test
    public void deveBuscarQuantidadeDeExamesPorNomeDoPaciente() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Exame> cq = cb.createQuery(Exame.class);
        Root<Exame> exame = cq.from(Exame.class);

        Join<Exame, Usuario> paciente = exame.join("paciente");

        cq.select(exame)
                .where(cb.like(
                        cb.lower(paciente.get("nome")),
                        "%maria%"
                ));

        List<Exame> exames = em.createQuery(cq).getResultList();

        assertEquals(5, exames.size());
        em.close();
    }

    // TESTES NOVO COM JPQL E CRITERIA
    // JPQL
    @Test
    public void deveBuscarQuantidadeDeExamesPorTipoDeUmPacienteAposData() {
        List<String> tiposValidosDeExame = List.of(
                "Mamografia", "Ultrassonografia Mamária", "Ressonância Magnética das Mamas",
                "Mamografia Digital", "Biópsia Guiada por USG", "Biópsia de Mama"
        );

        List<Object[]> resultados = em.createQuery(
                "SELECT e.tipo, COUNT(e) "
                + "FROM Exame e "
                + "JOIN e.paciente p "
                + "WHERE p.id = :idPaciente "
                + "AND e.dataExame >= :data "
                + "GROUP BY e.tipo "
                + "HAVING COUNT(e) > 0",
                Object[].class
        )
                .setParameter("idPaciente", 1L)
                .setParameter("data", java.sql.Date.valueOf("2024-01-01"))
                .getResultList();

        assertTrue(resultados.size() > 0);

        for (Object[] linha : resultados) {
            String tipo = (String) linha[0];
            Long quantidade = (Long) linha[1];

            assertTrue(!tipo.isBlank());
            assertTrue(quantidade > 0);
            assertTrue(tiposValidosDeExame.contains(tipo));

            Long totalExamesDoTipo = em.createQuery(
                    "SELECT COUNT(e) FROM Exame e "
                    + "WHERE e.paciente.id = :idPaciente "
                    + "AND e.tipo = :tipo "
                    + "AND e.dataExame >= :data",
                    Long.class
            )
                    .setParameter("idPaciente", 1L)
                    .setParameter("tipo", tipo)
                    .setParameter("data", java.sql.Date.valueOf("2024-01-01"))
                    .getSingleResult();

            assertEquals("Erro na contagem para o tipo: " + tipo, totalExamesDoTipo, quantidade);
        }
    }

    // CRITERIA
    @Test
    public void deveBuscarExamesPorTipoPeriodoENomeDoPacienteCriteria() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Exame> cq = cb.createQuery(Exame.class);
        Root<Exame> exame = cq.from(Exame.class);
        Join<Exame, Paciente> paciente = exame.join("paciente");

        String tipoExameEsperado = "Mamografia";
        String trechoNomePaciente = "Maria";
        Date dataInicio = java.sql.Date.valueOf("2023-01-01");
        Date dataFim = java.sql.Date.valueOf("2025-12-31");

        Predicate porTipo = cb.equal(exame.get("tipo"), tipoExameEsperado);
        Predicate porPeriodo = cb.between(exame.get("dataExame"), dataInicio, dataFim);
        Predicate porNomePaciente = cb.like(
                cb.lower(paciente.get("nome")),
                "%" + trechoNomePaciente.toLowerCase() + "%"
        );

        cq.select(exame)
                .where(cb.and(porTipo, porPeriodo, porNomePaciente))
                .orderBy(cb.desc(exame.get("dataExame")));

        List<Exame> exames = em.createQuery(cq).getResultList();

        assertTrue("Deveria retornar ao menos um exame", exames.size() > 0);

        Date dataAnterior = null;

        for (Exame e : exames) {
            assertEquals(tipoExameEsperado, e.getTipo());

            assertFalse(e.getDataExame().before(dataInicio));
            assertFalse(e.getDataExame().after(dataFim));

            assertNotNull(e.getPaciente());
            assertTrue(
                    e.getPaciente().getNome().toLowerCase().contains(trechoNomePaciente.toLowerCase())
            );

            if (dataAnterior != null) {
                assertFalse(
                        "A lista não está ordenada por data decrescente",
                        e.getDataExame().after(dataAnterior)
                );
            }

            dataAnterior = e.getDataExame();
        }

        em.close();
    }

}
