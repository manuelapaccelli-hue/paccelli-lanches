package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Usuario;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioDAO {

    public void salvar(Usuario usuario) {
        usuario.persist();
    }

    public Optional<Usuario> buscarAtivoPorId(Long id) {
        return Usuario.find("id = ?1 and ativo = true", id).firstResultOptional();
    }

    public Optional<Usuario> buscarAtivoPorEmail(String email) {
        return Usuario.find("email = ?1 and ativo = true", email).firstResultOptional();
    }

    public boolean existePorEmail(String email) {
        return Usuario.count("email", email) > 0;
    }

    public List<Usuario> listarAtivos() {
        return Usuario.list("ativo", Sort.ascending("nome"), true);
    }
}
