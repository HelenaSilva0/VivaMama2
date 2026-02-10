/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc.primeiraUnidade;

import com.vivamamadsc.vivamamadsc.Exame;
import com.vivamamadsc.vivamamadsc.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author necta
 */
public class ExameTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("vivamamadsc");
        // carrega dataset.xml no banco
        DbUnitUtil.inserirDados();
    }

    @AfterClass
    public static void tearDownClass() {
        emf.close();
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
        } else {
            et.rollback();
        }
        em.close();
    }

    @Test
    public void testPersistirExameNovo() {
        // paciente vindo da base (dataset.xml)
        Paciente paciente = em.find(Paciente.class, 1L);
        assertNotNull(paciente);

        Exame exame = new Exame();
        exame.setTipo("Mamografia controle");
        Calendar c = Calendar.getInstance();
        c.set(2025, Calendar.FEBRUARY, 15);
        exame.setDataExame(c.getTime());
        exame.setResultadoResumo("Controle anual de Mamografia controle.");
        exame.setPaciente(paciente);
        exame.setImagem("fake-image-novo".getBytes(StandardCharsets.UTF_8));

        em.persist(exame);
        em.flush();

        assertNotNull("Exame deve ter sido persistido com ID", exame.getId());
        assertEquals(paciente.getId(), exame.getPaciente().getId());
    }

    @Test
    public void testConsultarExameDoDataset() {
        // Exame que virá pronto do dataset.xml (ver seção do XML)
        Exame exame = em.find(Exame.class, 1L);

//        Exame exame = em.createQuery(
//                "SELECT e FROM Exame e " +
//                "WHERE e.paciente.id = :idPaciente AND e.tipo = :tipo",
//                Exame.class)
//            .setParameter("idPaciente", 1L)
//            .setParameter("tipo", "Mamografia")
//            .getSingleResult();
        assertNotNull(exame);
        assertEquals("Mamografia", exame.getTipo());
        assertEquals("Exame de rotina sem alterações.", exame.getResultadoResumo());
        assertNotNull(exame.getPaciente());
        assertEquals(Long.valueOf(1L), exame.getPaciente().getId());

        // imagem vinda do dataset (Base64 -> byte[])
        assertNotNull(exame.getImagem());
        assertTrue(exame.getImagem().length > 0);
        assertEquals("fake-image",
                new String(exame.getImagem(), StandardCharsets.UTF_8));
    }

    @Test
    public void testPersistirExameComImagemDeArquivo() throws Exception {
        Paciente paciente = em.find(Paciente.class, 1L);
        assertNotNull(paciente);

        Exame exame = new Exame();
        exame.setTipo("Mamografia com imagem");
        exame.setDataExame(new Date());
        exame.setResultadoResumo("Exame de Mamografia com imagem carregada do arquivo.");
        exame.setPaciente(paciente);

        // carrega a imagem da pasta src/test/resources/imagens
        byte[] imgBytes = loadResource("/imagens/mamografia.jpg");
        exame.setImagem(imgBytes);

        em.persist(exame);
        em.flush();

        assertNotNull(exame.getId());
        assertNotNull(exame.getImagem());
        assertTrue(exame.getImagem().length > 0);
    }

    private byte[] loadResource(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Recurso não encontrado: " + path);
            }
            return is.readAllBytes();
        }
    }
}
