/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Date;
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
public class MensagemTest {

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
    public void testPersistenciaMensagem() {
        // cria uma conversa (ou busca existente)
        Conversa conv = em.find(Conversa.class, 1L);
        if (conv == null) {
            conv = new Conversa();
            conv.setAssunto("Teste de mensagem");
            // adicionar participantes mínimos (usuario 1 e 2 se existirem)
            Usuario u1 = em.find(Usuario.class, 1L);
            Usuario u2 = em.find(Usuario.class, 2L);
            if (u1 != null) conv.adicionarParticipante(u1);
            if (u2 != null) conv.adicionarParticipante(u2);
            em.persist(conv);
        }

        // remetente (usuário 2)
        Usuario remetente = em.find(Usuario.class, 2L);

        Mensagem m = new Mensagem();
        m.setConversa(conv);
        m.setRemetente(remetente);
        m.setTexto("Mensagem de teste enviada em " + new Date());
        m.setEnviadoEm(new Date());
        m.setLida(false);

        em.persist(m);
        em.flush();

        assertNotNull("Mensagem deve receber id após persistir", m.getId());
        assertNotNull("Mensagem deve estar associada a uma conversa", m.getConversa());
    }

    @Test
    public void testConsultaMensagemById() {
        // espera que dataset contenha MENSAGEM com ID=1 (ver exemplo de dataset)
        Mensagem mm = em.find(Mensagem.class, 1L);
        assertNotNull("Mensagem com id=1 deve existir (ver dataset)", mm);
        assertNotNull("Mensagem deve ter remetente", mm.getRemetente());
        assertNotNull("Mensagem deve ter conversa associada", mm.getConversa());
    }
}
