package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.TipoUsuario;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoUsuarioDAO {

    public void salvar(TipoUsuario tipo) {
        tipo.persist();
    }

    public Optional<TipoUsuario> buscarPorNome(String nome) {
        return TipoUsuario.find("nome", nome).firstResultOptional();
    }

    public List<TipoUsuario> listarTodos() {
        return TipoUsuario.listAll(Sort.ascending("id"));
    }
}
