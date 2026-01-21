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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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

    @Test
    public void testBuscarPacientePorIdJPQL() {
        Paciente paciente = em.createQuery(
                "SELECT m FROM Paciente m WHERE m.id = :id", Paciente.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(paciente);
        assertEquals("Ciclano da Silva", paciente.getNome());
    }

    @Test
    public void testBuscarTodosPacientes() {
        List<Paciente> pacientes = em.createQuery(
                "SELECT m FROM Medico m", Paciente.class
        ).getResultList();

        assertFalse(pacientes.isEmpty());

    }

    @Test
    public void deveBuscarPacientePorNomeParcialECpf() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Paciente> cq = cb.createQuery(Paciente.class);
        Root<Paciente> paciente = cq.from(Paciente.class);

        Predicate nome = (Predicate) cb.like(paciente.get("nome"), "%Miriapode%");
        Predicate cpf = (Predicate) cb.equal(paciente.get("cpf"), "44444444444");

        cq.where(cb.and(nome, cpf));

        List<Paciente> resultado = em.createQuery(cq).getResultList();

        assertFalse(resultado.isEmpty());
        assertEquals(resultado.size(), 1);

        em.close();
    }

    @Test
    public void deveBuscarPacientesQuePossuemExames() {
        EntityManager em = emf.createEntityManager();
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

        em.close();
    }
}
