package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Lanche;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LancheDAO {

    public void salvar(Lanche lanche) {
        lanche.persist();
    }

    public List<Lanche> listarAtivos() {
        return Lanche.list("ativo", Sort.ascending("id"), true);
    }

    public Optional<Lanche> buscarAtivoPorId(Long id) {
        return Lanche.find("id = ?1 and ativo = true", id).firstResultOptional();
    }

    public Optional<Lanche> buscarAtivoPorNome(String nome) {
        return Lanche.find("nome = ?1 and ativo = true", nome).firstResultOptional();
    }

    public boolean existeAlgum() {
        return Lanche.count() > 0;
    }
}
