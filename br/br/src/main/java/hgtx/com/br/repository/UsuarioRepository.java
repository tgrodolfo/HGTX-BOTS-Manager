package hgtx.com.br.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hgtx.com.br.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Repository
public class UsuarioRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void save(Usuario usuario) {
        entityManager.persist(usuario);
    }

    public Usuario findById(Long id) {
        return entityManager.find(Usuario.class, id);
    }

    @Transactional
    public void delete(Usuario usuario) {
        entityManager.remove(usuario);
    }

    @Transactional
    public void update(Usuario usuario) {
        entityManager.merge(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        String jpql = "SELECT u FROM Usuario u WHERE u.email = :email";
        try {
            return Optional.of(entityManager.createQuery(jpql, Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
