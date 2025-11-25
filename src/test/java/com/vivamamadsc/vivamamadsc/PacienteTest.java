/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PacienteTest {

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
        }
        em.close();
    }

    @Test
    public void persistirUsuario() {
        Paciente usuario = criarUsuario("beltrano@gmail.com", "12345678777");

        em.persist(usuario);
        em.flush(); //forÃ§a que a persistÃªncia realizada vÃ¡ para o banco neste momento.

        assertNotNull(usuario.getId());
        assertNotNull(usuario.getHistoricoFamiliarPdf());
        assertEquals("12345",
                new String(usuario.getHistoricoFamiliarPdf(), StandardCharsets.UTF_8));
    }

    @Test
    public void consultarUsuario() {
        Paciente paciente = em.find(Paciente.class, 1L);
        assertEquals("ciclano@gmail.com", paciente.getEmail());
        assertEquals("Ciclano da Silva", paciente.getNome());
    }

    @Test
    public void persistirPacienteComPdfDeArquivo() throws Exception {
        Paciente paciente = criarUsuario("birigui@teste.com", "09876543219");

        // carrega o PDF da pasta src/test/resources/pdf
        byte[] pdfBytes = loadResource("/pdf/historico.pdf");
        paciente.setHistoricoFamiliarPdf(pdfBytes);

        em.persist(paciente);
        em.flush();

        assertNotNull(paciente.getId());
        assertNotNull(paciente.getHistoricoFamiliarPdf());
        assertTrue(paciente.getHistoricoFamiliarPdf().length > 0);
    }

    private Paciente criarUsuario(String email, String cpf) {
        Paciente paciente = new Paciente();

        paciente.setNome("Beltrano da Silva");
        paciente.setCpf(cpf);
        paciente.setEmail(email);
        paciente.setSenha("teste");
        paciente.setTipo(TipoUsuario.PACIENTE);
        paciente.setHistoricoFamiliar("Sem histórico familiar.");

        paciente.setHistoricoFamiliarPdf("12345".getBytes(StandardCharsets.UTF_8));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1981);
        c.set(Calendar.MONTH, Calendar.FEBRUARY);
        c.set(Calendar.DAY_OF_MONTH, 25);
        paciente.setDataNascimento(c.getTime());

        return paciente;
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
