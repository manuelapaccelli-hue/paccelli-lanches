package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.ItemCarrinhoDAO;
import br.com.paccellilanches.dao.ItemPedidoDAO;
import br.com.paccellilanches.dao.PedidoDAO;
import br.com.paccellilanches.dto.AlterarStatusPedidoDTO;
import br.com.paccellilanches.dto.ItemPedidoDTO;
import br.com.paccellilanches.dto.Moeda;
import br.com.paccellilanches.dto.PedidoDTO;
import br.com.paccellilanches.dto.PedidoGestaoDTO;
import br.com.paccellilanches.dto.StatusPedidoDTO;
import br.com.paccellilanches.entity.ItemCarrinho;
import br.com.paccellilanches.entity.ItemPedido;
import br.com.paccellilanches.entity.Pedido;
import br.com.paccellilanches.entity.Promocao;
import br.com.paccellilanches.entity.StatusPedido;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Regras dos pedidos: o cliente finaliza a compra a partir do carrinho e acompanha os seus pedidos;
 * o administrador vê todos e muda o status.
 */
@ApplicationScoped
public class PedidoBO {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject
    PedidoDAO pedidoDAO;

    @Inject
    ItemPedidoDAO itemPedidoDAO;

    @Inject
    ItemCarrinhoDAO itemCarrinhoDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Inject
    PromocaoBO promocaoBO;

    /**
     * Transforma o carrinho do usuário em um pedido, com os preços de agora (promoções vigentes
     * já aplicadas), e esvazia o carrinho.
     */
    @Transactional
    public PedidoDTO finalizar(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        List<ItemCarrinho> itensCarrinho = itemCarrinhoDAO.listarDoUsuario(usuario);
        if (itensCarrinho.isEmpty()) {
            throw new NegocioException("Seu carrinho está vazio. Adicione lanches antes de finalizar o pedido.");
        }

        Map<Long, Promocao> promocoes = promocaoBO.promocoesVigentesPorLanche();
        Pedido pedido = new Pedido(usuario, LocalDateTime.now());
        for (ItemCarrinho itemCarrinho : itensCarrinho) {
            BigDecimal preco = promocaoBO.precoVigente(itemCarrinho.lanche, promocoes);
            pedido.itens.add(new ItemPedido(pedido, itemCarrinho.lanche, itemCarrinho.quantidade, preco));
        }
        pedido.total = pedido.itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedidoDAO.salvar(pedido);
        pedido.itens.forEach(itemPedidoDAO::salvar);
        itemCarrinhoDAO.esvaziar(usuario);
        return paraDTO(pedido);
    }

    /** Pedidos do usuário logado, dos mais recentes aos mais antigos. */
    public List<PedidoDTO> meusPedidos(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        return pedidoDAO.listarDoUsuario(usuario)
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    /** Todos os pedidos, para a tela de gestão (somente administradores). */
    public List<PedidoGestaoDTO> listarParaGestao(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        return pedidoDAO.listarTodos()
                .stream()
                .map(this::paraGestaoDTO)
                .toList();
    }

    public List<StatusPedidoDTO> statusDisponiveis() {
        return Arrays.stream(StatusPedido.values())
                .map(status -> new StatusPedidoDTO(status.name(), status.titulo))
                .toList();
    }

    /**
     * Muda o status de um pedido (somente administradores). Pedido entregue ou cancelado não muda
     * mais, e o status não pode voltar para uma etapa anterior; cancelar é permitido a qualquer momento
     * antes da entrega.
     */
    @Transactional
    public void alterarStatus(String sessionId, Long id, AlterarStatusPedidoDTO request) {
        autenticacaoBO.exigirAdmin(sessionId);
        Pedido pedido = pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
        StatusPedido novo = converterStatus(request.status());

        if (pedido.status.encerrado()) {
            throw new NegocioException("O pedido #" + pedido.id + " já foi " + pedido.status.titulo.toLowerCase()
                    + " e não pode mais mudar de status.");
        }
        if (novo != StatusPedido.CANCELADO && novo.ordinal() < pedido.status.ordinal()) {
            throw new NegocioException("O pedido não pode voltar de \"" + pedido.status.titulo
                    + "\" para \"" + novo.titulo + "\".");
        }
        pedido.status = novo;
    }

    private StatusPedido converterStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new NegocioException("Escolha o novo status do pedido.");
        }
        try {
            return StatusPedido.valueOf(status.trim());
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Status de pedido inválido.");
        }
    }

    private PedidoDTO paraDTO(Pedido pedido) {
        return new PedidoDTO(pedido.id, pedido.dataHora.format(DATA_HORA), pedido.status.titulo,
                statusCss(pedido.status), Moeda.formatar(pedido.total), itensDTO(pedido));
    }

    private PedidoGestaoDTO paraGestaoDTO(Pedido pedido) {
        return new PedidoGestaoDTO(pedido.id, pedido.usuario.nome, pedido.usuario.email,
                pedido.dataHora.format(DATA_HORA), pedido.status.titulo, pedido.status.name(),
                statusCss(pedido.status), pedido.status.encerrado(), Moeda.formatar(pedido.total), itensDTO(pedido));
    }

    private List<ItemPedidoDTO> itensDTO(Pedido pedido) {
        return pedido.itens.stream()
                .map(item -> new ItemPedidoDTO(item.nomeLanche, item.quantidade,
                        Moeda.formatar(item.precoUnitario), Moeda.formatar(item.subtotal())))
                .toList();
    }

    /** Status em formato de classe CSS (ex.: EM_PREPARO vira "em-preparo"). */
    private String statusCss(StatusPedido status) {
        return status.name().toLowerCase().replace('_', '-');
    }
}
