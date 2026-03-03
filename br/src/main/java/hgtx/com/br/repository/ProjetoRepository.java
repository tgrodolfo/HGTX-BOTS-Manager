package hgtx.com.br.repository;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hgtx.com.br.model.Usuario;
import hgtx.com.br.model.Bot;
import hgtx.com.br.model.Projeto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@Repository
public class ProjetoRepository {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BotRepository botRepository;

    @Transactional
    public void save(Projeto projeto) {
        entityManager.persist(projeto);
    }
    public Projeto findById(Long id) {
        return entityManager.find(Projeto.class, id);
    }
    @Transactional
    public void deleteById(Long id) {
        Projeto projeto = findById(id);
        Usuario usuario = projeto.getUsuario();
        List<Bot> bots = botRepository.findAllByUserEmail(usuario.getEmail());
        for (Bot bot : bots) {
            botRepository.delete(bot);
        }
        if (projeto != null) {
            entityManager.remove(projeto);
        }
    }
    @Transactional
    public void update(Projeto projeto) {
        entityManager.merge(projeto);
    }
    public List<Projeto> findAllByUser(Long userid) {
        List<Projeto> oi = entityManager.createQuery("SELECT p FROM Projeto p WHERE p.usuario.id = :userId", Projeto.class)
            .setParameter("userId", userid)
            .getResultList();
        return oi;
    }
}
