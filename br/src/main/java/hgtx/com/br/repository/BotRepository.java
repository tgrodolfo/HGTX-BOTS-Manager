package hgtx.com.br.repository;

import java.util.List;

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
        entityManager.remove(entityManager.contains(bot) ? bot : entityManager.merge(bot));
    }

    @Transactional
    public Bot update(Bot bot) {
        return entityManager.merge(bot);
    }

    public int contbotsbyUserEmail(String userEmail) {
        String jpql = "SELECT COUNT(b) FROM Bot b JOIN b.usuario u WHERE u.email = :email";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("email", userEmail)
                .getSingleResult();
        return count.intValue();
    }

    public List<Bot> findAllByUserEmail(String userEmail) {
        String jpql = "SELECT b FROM Bot b JOIN b.usuario u WHERE u.email = :email";
        return entityManager.createQuery(jpql, Bot.class)
                .setParameter("email", userEmail)
                .getResultList();
    }

    public List<Bot> findnoprojectByUserEmail(String userEmail) {
        String jpql = "SELECT b FROM Bot b JOIN b.usuario u WHERE u.email = :email AND b.projeto IS NULL";
        return entityManager.createQuery(jpql, Bot.class)
                .setParameter("email", userEmail)
                .getResultList();
    }
}

