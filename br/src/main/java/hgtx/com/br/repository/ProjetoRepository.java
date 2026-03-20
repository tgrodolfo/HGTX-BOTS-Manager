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

        if (projeto == null) {
            return;
        }

        // Remove o vínculo com usuários
        for (Usuario usuario : projeto.getUsuarios()) {
            usuario.getProjetos().remove(projeto);
        }

        // Se quiser deletar bots junto:
        for (Bot bot : projeto.getBots()) {
            botRepository.delete(bot);
        }

        entityManager.remove(projeto);
    }

    @Transactional
    public void update(Projeto projeto) {
        entityManager.merge(projeto);
    }

    public List<Projeto> findAllByUser(Long userid) {
        return entityManager
                .createQuery(
                        "SELECT DISTINCT p FROM Projeto p JOIN p.usuarios u WHERE u.id = :userId",
                        Projeto.class)
                .setParameter("userId", userid)
                .getResultList();
    }

    @Transactional
    public void compartilharProjeto(Long projetoId, Usuario usuario) {

        Projeto projeto = entityManager.find(Projeto.class, projetoId);

        if (projeto == null) {
            throw new RuntimeException("Projeto não encontrado");
        }

        // evita duplicidade
        if (!projeto.getUsuarios().contains(usuario)) {
            projeto.getUsuarios().add(usuario);
        }

        entityManager.merge(projeto);
    }

    @Transactional
    public void removerCompartilhamento(Long projetoId, Long usuarioId) {

        Projeto projeto = entityManager.find(Projeto.class, projetoId);
        Usuario usuario = entityManager.find(Usuario.class, usuarioId);

        if (projeto == null || usuario == null)
            return;

        projeto.getUsuarios().remove(usuario);
        usuario.getProjetos().remove(projeto);

        entityManager.merge(projeto);
    }
}
