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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Vivamamadsc {

    private static final Logger LOG = Logger.getLogger(Vivamamadsc.class.getName());

    private static final EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("vivamamadsc");

    public static void main(String[] args) throws IOException {

        try {
            Long idPaciente = persistirPaciente();

            Especialidade oncologia = new Especialidade();
            oncologia.setNome("Oncologia");
            persistir(oncologia);
            
            Especialidade mastologia = new Especialidade();
            mastologia.setNome("Mastologia");
            persistir(mastologia);

            Set<Especialidade> especialidades = new HashSet<>();
            especialidades.add(oncologia);
            especialidades.add(mastologia);

            Long idMedico = persistirMedico(especialidades);

            consultarPaciente(idPaciente);
            consultarMedico(idMedico);
        } finally {
            emf.close();
        }
    }

    private static Long persistirPaciente() throws IOException {

        Usuario u = new Usuario();
        Paciente p = new Paciente();
        preencherPaciente(u, p);
        u.setPaciente(p);                 // seta o paciente dentro do usuário
        u.setTipo(TipoUsuario.PACIENTE);
        return persistir(u);
    }

    private static Long persistirMedico(Set<Especialidade> especialidades) throws IOException {
        Usuario u = new Usuario();
        Medico m = new Medico();
        preencherMedico(u, m, especialidades);
        u.setMedico(m);
        u.setTipo(TipoUsuario.MEDICO);
        return persistir(u);
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

    private static void preencherPaciente(Usuario u, Paciente p) throws IOException {
        u.setNome("Fulano da Silva");
        u.setCpf("98765432100");
        u.setEmail("fulano@gmail.com");
        u.setSenha("senha");
//        p.setHistoricoFamiliar(" avo com cancer de mama.");
        p.setDataNascimento(new GregorianCalendar(2004, Calendar.APRIL, 23).getTime());
        p.setUsuario(u);
    }

    private static void preencherMedico(Usuario u, Medico m, Set<Especialidade> especialidades) throws IOException {
        u.setNome("Dra. Maria");
        u.setCpf("12345678901");
        u.setEmail("medicofulano@gmail.com");
        u.setSenha("senha");
        m.setCrm("123456");
        m.setUsuario(u);
        for (Especialidade e : especialidades) {
            m.addEspecialidade(e);
        }
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
            
            Usuario u = p.getUsuario();
            
            LOG.log(Level.INFO, """
                    Paciente:
                      id={0}
                      nome={1}
                      email={2} 
                      cpf={3}   
                      nascimento={3}
                      historicoFamiliar={4}
                    """,
                    new Object[]{
                        p.getId(),
                        u != null ? u.getNome() : null,
                        u != null ? u.getEmail() : null,
                        u != null ? u.getCpf() : null,
                        p.getDataNascimento(),
                        p.getHistoricoFamiliar()
                    });
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
            
             Usuario u = m.getUsuario();
             
             String especialidadesStr = m.getEspecialidades() == null
                    ? ""
                    : m.getEspecialidades()
                        .stream()
                        .map(Especialidade::getNome)
                        .collect(Collectors.joining(", "));
            
            LOG.log(Level.INFO, """
                    Medico:
                      id={0}
                      nome={1}
                      email={2}
                      cpf={3}
                      crm={3}
                      especialidades={5}
                    """,
                    new Object[]{
                       m.getId(),
                        u != null ? u.getNome() : null,
                        u != null ? u.getEmail() : null,
                        u != null ? u.getCpf() : null,
                        m.getCrm(),
                        especialidadesStr
                    });
        } finally {
            em.close();
        }
    }
}
