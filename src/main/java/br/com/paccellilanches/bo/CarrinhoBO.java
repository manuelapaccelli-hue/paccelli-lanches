package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.ItemCarrinhoDAO;
import br.com.paccellilanches.dao.LancheDAO;
import br.com.paccellilanches.dto.AdicionarItemDTO;
import br.com.paccellilanches.dto.CarrinhoDTO;
import br.com.paccellilanches.dto.ItemCarrinhoDTO;
import br.com.paccellilanches.dto.Moeda;
import br.com.paccellilanches.dto.QuantidadeDTO;
import br.com.paccellilanches.entity.ItemCarrinho;
import br.com.paccellilanches.entity.Lanche;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regras do carrinho de compras: cada usuário logado tem o seu, guardado no banco.
 */
@ApplicationScoped
public class CarrinhoBO {

    private static final int QUANTIDADE_MAXIMA = 99;

    @Inject
    ItemCarrinhoDAO itemCarrinhoDAO;

    @Inject
    LancheDAO lancheDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    public CarrinhoDTO carrinho(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        List<ItemCarrinho> itens = itemCarrinhoDAO.listarDoUsuario(usuario);

        BigDecimal total = itens.stream()
                .map(ItemCarrinho::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int quantidadeItens = itens.stream()
                .mapToInt(item -> item.quantidade)
                .sum();

        return new CarrinhoDTO(itens.stream().map(this::paraDTO).toList(), quantidadeItens, Moeda.formatar(total));
    }

    /** Adiciona uma unidade do lanche; se ele já estiver no carrinho, soma à quantidade. */
    @Transactional
    public void adicionar(String sessionId, AdicionarItemDTO request) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        Lanche lanche = lancheDAO.buscarAtivoPorId(request.lancheId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lanche não encontrado no cardápio."));

        itemCarrinhoDAO.buscar(usuario, lanche.id).ifPresentOrElse(
                item -> item.quantidade = validarQuantidade(item.quantidade + 1),
                () -> itemCarrinhoDAO.salvar(new ItemCarrinho(usuario, lanche, 1)));
    }

    @Transactional
    public void alterarQuantidade(String sessionId, Long lancheId, QuantidadeDTO request) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        buscarItem(usuario, lancheId).quantidade = validarQuantidade(request.quantidade());
    }

    @Transactional
    public void remover(String sessionId, Long lancheId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        itemCarrinhoDAO.remover(buscarItem(usuario, lancheId));
    }

    private int validarQuantidade(int quantidade) {
        if (quantidade < 1) {
            throw new NegocioException("A quantidade deve ser de pelo menos 1.");
        }
        if (quantidade > QUANTIDADE_MAXIMA) {
            throw new NegocioException("A quantidade máxima por item é " + QUANTIDADE_MAXIMA + ".");
        }
        return quantidade;
    }

    private ItemCarrinho buscarItem(Usuario usuario, Long lancheId) {
        return itemCarrinhoDAO.buscar(usuario, lancheId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado no carrinho."));
    }

    private ItemCarrinhoDTO paraDTO(ItemCarrinho item) {
        return new ItemCarrinhoDTO(item.lanche.id, item.lanche.nome, item.lanche.descricao, item.quantidade,
                Moeda.formatar(item.lanche.preco), Moeda.formatar(item.subtotal()));
    }
}
