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
import br.com.paccellilanches.entity.Promocao;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @Inject
    PromocaoBO promocaoBO;

    /** Carrinho do usuário com os preços de hoje: lanches em promoção entram com o desconto. */
    public CarrinhoDTO carrinho(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        List<ItemCarrinho> itens = itemCarrinhoDAO.listarDoUsuario(usuario);
        Map<Long, Promocao> promocoes = promocaoBO.promocoesVigentesPorLanche();

        BigDecimal total = itens.stream()
                .map(item -> subtotal(item, promocoes))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int quantidadeItens = itens.stream()
                .mapToInt(item -> item.quantidade)
                .sum();

        return new CarrinhoDTO(itens.stream().map(item -> paraDTO(item, promocoes)).toList(), quantidadeItens, Moeda.formatar(total));
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

    private BigDecimal subtotal(ItemCarrinho item, Map<Long, Promocao> promocoes) {
        return promocaoBO.precoVigente(item.lanche, promocoes).multiply(BigDecimal.valueOf(item.quantidade));
    }

    private ItemCarrinhoDTO paraDTO(ItemCarrinho item, Map<Long, Promocao> promocoes) {
        Lanche lanche = item.lanche;
        boolean emPromocao = promocoes.containsKey(lanche.id);
        return new ItemCarrinhoDTO(lanche.id, lanche.nome, lanche.descricao, item.quantidade,
                Moeda.formatar(promocaoBO.precoVigente(lanche, promocoes)),
                emPromocao ? Moeda.formatar(lanche.preco) : null,
                Moeda.formatar(subtotal(item, promocoes)));
    }
}
