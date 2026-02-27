package hgtx.com.br.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hgtx.com.br.model.Bot;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class BotRepository {
  
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void save(Bot bot) {
        entityManager.persist(bot);
    }
    public Bot findById(Long id) {
        return entityManager.find(Bot.class, id);
    }
    @Transactional
    public void delete(Bot bot) {
        entityManager.remove(bot);
    }
    @Transactional
    public void update(Bot bot) {
        entityManager.merge(bot);
    }

    public int contbotsbyUserEmail(String UserEmail) {
        String jpql = "SELECT COUNT(b) FROM Bot b WHERE b.usuario.email = :email";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("email", UserEmail)
                .getSingleResult();
        return count.intValue();
    }

    public java.util.List<Bot> findAllByUserEmail(String userEmail) {
        String jpql = "SELECT b FROM Bot b WHERE b.usuario.email = :email";
        return entityManager.createQuery(jpql, Bot.class)
                .setParameter("email", userEmail)
                .getResultList();
    }

    
   

    
}
