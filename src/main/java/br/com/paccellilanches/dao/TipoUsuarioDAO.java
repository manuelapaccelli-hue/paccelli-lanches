package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.TipoUsuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoUsuarioDAO implements PanacheRepository<TipoUsuario> {

    public void salvar(TipoUsuario tipo) {
        persist(tipo);
    }

    public Optional<TipoUsuario> buscarPorNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }

    public List<TipoUsuario> listarTodos() {
        return listAll(Sort.ascending("id"));
    }
}
