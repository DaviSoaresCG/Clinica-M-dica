package com.example.aula4.Repository;

import com.example.aula4.Model.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicoRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Medico> medicoList() {
        Query query = em.createQuery("from Medico", Medico.class);
        return query.getResultList();
    }

    public void save(Medico medico) {
        em.persist(medico);
    }

    public Medico medico(Long id) {
        return em.find(Medico.class, id);
    }

    public void remove(Long id) {
        Medico m = em.find(Medico.class, id);
        if (m != null) {
            em.remove(m);
        }
    }

    public void update(Medico medico) {
        em.merge(medico);
    }
}
