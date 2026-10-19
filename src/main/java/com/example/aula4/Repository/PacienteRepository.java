package com.example.aula4.Repository;

import com.example.aula4.Model.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PacienteRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Paciente> pacienteList() {
        Query query = em.createQuery("from Paciente", Paciente.class);
        return query.getResultList();
    }

    public void save(Paciente paciente) {
        em.persist(paciente);
    }

    public Paciente paciente(Long id) {
        return em.find(Paciente.class, id);
    }

    public void remove(Long id) {
        Paciente p = em.find(Paciente.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    public void update(Paciente paciente) {
        em.merge(paciente);
    }
}
