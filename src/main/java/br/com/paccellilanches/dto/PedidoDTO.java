package br.com.paccellilanches.dto;

import java.util.List;

/** Pedido visto pelo cliente (tela "Meus pedidos" e resposta ao finalizar a compra). */
public record PedidoDTO(Long id, String dataHora, String status, String statusCss, String total,
                        List<ItemPedidoDTO> itens) {
}
