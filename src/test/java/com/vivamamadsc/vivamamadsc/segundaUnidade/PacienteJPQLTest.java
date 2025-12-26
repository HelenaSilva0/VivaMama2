/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.segundaUnidade;

import com.vivamamadsc.vivamamadsc.Paciente;
import com.vivamamadsc.vivamamadsc.primeiraUnidade.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
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
    public void testAtualizarPaciente() {
        TypedQuery<Paciente> query = em.createNamedQuery("Paciente.PorNome", Paciente.class);

        query.setParameter("nome", "Ciclano da Silva");

        Paciente paciente = query.getSingleResult();

        assertNotNull(paciente);

        paciente.setNome("Klaus Meine");

        em.flush(); // Atualização vai rolar aqui

        assertEquals(0, query.getResultList().size());

        // Verificando se realmente atualizou
        query.setParameter("nome", "Klaus Meine");

        paciente = query.getSingleResult();

        assertNotNull(paciente);
    }

    @Test
    public void testRemoverPaciente() {
        TypedQuery<Paciente> query = em.createNamedQuery("Paciente.PorNome", Paciente.class);
        
        query.setParameter("nome", "Miriapode da Silva");
        
        Paciente paciente = query.getSingleResult();

        assertNotNull(paciente);
        
        em.remove(paciente);
        em.flush();
        
        assertEquals(0, query.getResultList().size());
    }
}
