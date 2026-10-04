package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Promocao;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PromocaoDAO {

    public void salvar(Promocao promocao) {
        promocao.persist();
    }

    /**
     * Promoções ativas, de lanches ativos, cujo período inclui a data informada. Promoções que
     * deixaram de ser desconto (o preço do lanche foi reduzido abaixo delas) são ignoradas.
     */
    public List<Promocao> listarVigentes(LocalDate data) {
        return Promocao.list("ativo = true and lanche.ativo = true and dataInicio <= ?1 and dataFim >= ?1 "
                + "and precoPromocional < lanche.preco", Sort.ascending("id"), data);
    }

    /** Promoções não encerradas de lanches ativos, das mais recentes para as mais antigas. */
    public List<Promocao> listarAtivas() {
        return Promocao.list("ativo = true and lanche.ativo = true",
                Sort.descending("dataInicio").and("id", Sort.Direction.Descending));
    }

    public Optional<Promocao> buscarAtivaPorId(Long id) {
        return Promocao.find("id = ?1 and ativo = true", id).firstResultOptional();
    }

    public boolean existeAlguma() {
        return Promocao.count() > 0;
    }
}
