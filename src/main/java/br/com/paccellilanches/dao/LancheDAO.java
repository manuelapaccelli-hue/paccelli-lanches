package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Lanche;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LancheDAO implements PanacheRepository<Lanche> {

    public void salvar(Lanche lanche) {
        persist(lanche);
    }

    public List<Lanche> listarAtivos() {
        return list("ativo", Sort.ascending("id"), true);
    }

    public Optional<Lanche> buscarAtivoPorId(Long id) {
        return find("id = ?1 and ativo = true", id).firstResultOptional();
    }

    public Optional<Lanche> buscarAtivoPorNome(String nome) {
        return find("nome = ?1 and ativo = true", nome).firstResultOptional();
    }

    public boolean existeAlgum() {
        return count() > 0;
    }
}
