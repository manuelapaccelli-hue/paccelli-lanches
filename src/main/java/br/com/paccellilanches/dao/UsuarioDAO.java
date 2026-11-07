package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioDAO implements PanacheRepository<Usuario> {

    public void salvar(Usuario usuario) {
        persist(usuario);
    }

    public Optional<Usuario> buscarAtivoPorId(Long id) {
        return find("id = ?1 and ativo = true", id).firstResultOptional();
    }

    public Optional<Usuario> buscarAtivoPorEmail(String email) {
        return find("email = ?1 and ativo = true", email).firstResultOptional();
    }

    public boolean existePorEmail(String email) {
        return count("email", email) > 0;
    }

    /** Se outro usuário (diferente do id informado) já usa o e-mail. */
    public boolean existeOutroComEmail(String email, Long id) {
        return count("email = ?1 and id <> ?2", email, id) > 0;
    }

    /** Se algum usuário (exceto o do id informado, quando não for null) já usa o CPF. */
    public boolean existeOutroComCpf(String cpf, Long id) {
        return id == null
                ? count("cpf", cpf) > 0
                : count("cpf = ?1 and id <> ?2", cpf, id) > 0;
    }

    public List<Usuario> listarAtivos() {
        return list("ativo", Sort.ascending("nome"), true);
    }
}
