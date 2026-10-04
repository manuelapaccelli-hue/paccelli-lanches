package br.com.paccellilanches.bootstrap;

import br.com.paccellilanches.dao.LancheDAO;
import br.com.paccellilanches.dao.PromocaoDAO;
import br.com.paccellilanches.entity.Promocao;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Na primeira execução, com a tabela vazia, cadastra as promoções iniciais, válidas por um mês.
 */
@ApplicationScoped
public class PromocaoBootstrap {

    @Inject
    PromocaoDAO promocaoDAO;

    @Inject
    LancheDAO lancheDAO;

    // Prioridade maior que a padrão (2500) para rodar depois do cadastro dos lanches no LancheBO
    @Transactional
    void popularPromocoesIniciais(@Observes @Priority(3000) StartupEvent evento) {
        if (promocaoDAO.existeAlguma()) {
            return;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate fim = hoje.plusMonths(1);
        cadastrar("Paccelli Clássico", new BigDecimal("9.90"), null, hoje, fim);
        cadastrar("Paccelli Supremo", new BigDecimal("24.90"), "TOP", hoje, fim);
    }

    private void cadastrar(String nomeLanche, BigDecimal precoPromocional, String selo, LocalDate inicio, LocalDate fim) {
        lancheDAO.buscarAtivoPorNome(nomeLanche).ifPresent(lanche ->
                promocaoDAO.salvar(new Promocao(lanche, precoPromocional, selo, inicio, fim)));
    }
}
