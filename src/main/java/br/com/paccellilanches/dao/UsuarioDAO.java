package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioDAO {

    @Transactional
    public void salvar(Usuario usuario) {
        usuario.persist();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return Usuario.find("email", email).firstResultOptional();
    }

    public boolean existePorEmail(String email) {
        return Usuario.count("email", email) > 0;
    }

    public List<Usuario> listarTodos() {
        return Usuario.listAll();
    }
}
