package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Promocao;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PromocaoDAO {

    public void salvar(Promocao promocao) {
        promocao.persist();
    }

    /** Promoções ativas, de lanches ativos, cujo período inclui a data informada. */
    public List<Promocao> listarVigentes(LocalDate data) {
        return Promocao.list("ativo = true and lanche.ativo = true and dataInicio <= ?1 and dataFim >= ?1",
                Sort.ascending("id"), data);
    }

    public boolean existeAlguma() {
        return Promocao.count() > 0;
    }
}
