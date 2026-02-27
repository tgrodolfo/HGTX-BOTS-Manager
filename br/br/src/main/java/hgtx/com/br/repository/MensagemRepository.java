package hgtx.com.br.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hgtx.com.br.model.Mensagem;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class MensagemRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void save(Mensagem mensagem) {
        entityManager.persist(mensagem);
    }

    @Transactional
    public Mensagem findById(Long id) {
        return entityManager.find(Mensagem.class, id);
    }

    @Transactional
    public void delete(Mensagem mensagem) {
        entityManager.remove(mensagem);
    }

    public List<Mensagem> findAllByBotId(Long botId) {
        return entityManager.createQuery("SELECT m FROM Mensagem m WHERE m.bot.id = :botId", Mensagem.class)
                .setParameter("botId", botId)
                .getResultList();
    }
    @Transactional
    public void saveMensagem(Mensagem mensagem) {
        entityManager.persist(mensagem);
    }
}
