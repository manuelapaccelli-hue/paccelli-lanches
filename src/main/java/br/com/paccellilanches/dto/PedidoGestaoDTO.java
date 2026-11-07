package br.com.paccellilanches.dto;

import java.util.List;

/** Pedido visto pelo administrador, com o cliente e o código do status para o formulário. */
public record PedidoGestaoDTO(Long id, String cliente, String emailCliente, String dataHora, String status,
                              String statusCodigo, String statusCss, boolean encerrado, String total,
                              List<ItemPedidoDTO> itens) {
}
