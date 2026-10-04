package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Lanche;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LancheDAO {

    public void salvar(Lanche lanche) {
        lanche.persist();
    }

    public List<Lanche> listarAtivos() {
        return Lanche.list("ativo", Sort.ascending("id"), true);
    }

    public boolean existeAlgum() {
        return Lanche.count() > 0;
    }
}
