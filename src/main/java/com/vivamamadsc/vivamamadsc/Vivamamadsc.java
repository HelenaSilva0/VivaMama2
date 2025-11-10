/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vivamamadsc.vivamamadsc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vivamamadsc {

    private static final Logger LOG = Logger.getLogger(Vivamamadsc.class.getName());

    private static final EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("vivamamadsc");

    public static void main(String[] args) throws IOException {

        try {
            Long idPaciente = persistirPaciente();
            Long idMedico = persistirMedico();

            consultarPaciente(idPaciente);
            consultarMedico(idMedico);
        } finally {
            emf.close();
        }
    }

    private static Long persistirPaciente() throws IOException {

        Paciente p = new Paciente();
        preencherPaciente(p);
        return persistir(p);
    }

    private static Long persistirMedico() throws IOException{
        Medico m = new Medico();
        preencherMedico(m);
        return persistir(m);
    }
    
    private static <T> Long persistir(T entidade) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            em.persist(entidade);
            tx.commit();

            Object id = emf.getPersistenceUnitUtil().getIdentifier(entidade);
            LOG.log(Level.INFO, "Entidade persistida: {0} com id={1}",
                    new Object[]{entidade.getClass().getSimpleName(), id});
            return (Long) id;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOG.log(Level.SEVERE, "Erro ao persistir " + entidade.getClass().getSimpleName(), ex);
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /*Paciente p = new Paciente();
        preencherPaciente(p);
        //EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction et = null;
        try {

            //emf = Persistence.createEntityManagerFactory("vivamamadsc");
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.persist(p);
            et.commit();
        } catch (Exception ex) {
            if (et != null && et.isActive) {
                et.rollback();
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }*/

 /* private static void preencherUsuario(Usuario usuario) {
        usuario.setNome("Fulano da Silva");
        usuario.setEmail("fulano@gmail.com");
        usuario.setSenha("teste"); 
        usuario.setDataNascimento(new Date());
       
    }*/
    private static void preencherPaciente(Paciente p) throws IOException {
        p.setNome("Fulano da Silva");
        p.setEmail("fulano@gmail.com");
        p.setSenha("senha");
        p.setHistoricoFamiliar(" avo com cancer de mama.");
        p.setDataNascimento(new GregorianCalendar(2004, Calendar.APRIL, 23).getTime());
    }

    private static void preencherMedico(Medico m) throws IOException {
        m.setNome("Dra. Maria");
        m.setEmail("medicofulano@gmail.com");
        m.setSenha("senha");
        m.setCrm("123456");
        m.setEspecialidade("mastologia");
    }

    private static void consultarPaciente(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            LOG.info("Consultando Paciente por ID...");
            Paciente p = em.find(Paciente.class, id);
            if (p == null) {
                LOG.log(Level.WARNING, "Paciente id={0} não encontrado.", id);
                return;
            }
            LOG.log(Level.INFO, """
                    Paciente:
                      id={0}
                      nome={1}
                      email={2}
                      nascimento={3}
                      historicoFamiliar={4}
                    """,
                    new Object[]{p.getId(), p.getNome(), p.getEmail(),
                        p.getDataNascimento(), p.getHistoricoFamiliar()});
        } finally {
            em.close();
        }
    }

    private static void consultarMedico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            LOG.info("Consultando Medico por ID...");
            Medico m = em.find(Medico.class, id);
            if (m == null) {
                LOG.log(Level.WARNING, "Medico id={0} não encontrado.", id);
                return;
            }
            LOG.log(Level.INFO, """
                    Medico:
                      id={0}
                      nome={1}
                      email={2}
                      crm={3}
                      especialidade={4}
                    """,
                    new Object[]{m.getId(), m.getNome(), m.getEmail(),
                        m.getCrm(), m.getEspecialidade()});
        } finally {
            em.close();
        }
    }
}
