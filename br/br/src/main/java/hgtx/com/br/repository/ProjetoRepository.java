package hgtx.com.br.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hgtx.com.br.model.Projeto;
import jakarta.persistence.EntityManager;

@Repository
public class ProjetoRepository {
  
    @Autowired
    private EntityManager entityManager;

    public void save(Projeto projeto) {
        entityManager.persist(projeto);
    }
    public Projeto findById(Long id) {
        return entityManager.find(Projeto.class, id);
    }
    public void delete(Projeto projeto) {
        entityManager.remove(projeto);
    }
    public void update(Projeto projeto) {
        entityManager.merge(projeto);
    }
    
}
