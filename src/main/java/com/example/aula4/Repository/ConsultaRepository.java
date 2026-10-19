package com.example.aula4.Repository;

import com.example.aula4.Model.Consulta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConsultaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Consulta> consultaList() {
        Query query = em.createQuery("from Consulta", Consulta.class);
        return query.getResultList();
    }

    public void save(Consulta consulta) {
        em.persist(consulta);
    }

    public Consulta consulta(Long id) {
        return em.find(Consulta.class, id);
    }

    public void remove(Long id) {
        Consulta c = em.find(Consulta.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    public void update(Consulta consulta) {
        em.merge(consulta);
    }
}
