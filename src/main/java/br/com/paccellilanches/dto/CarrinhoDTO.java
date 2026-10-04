package br.com.paccellilanches.dto;

import java.util.List;

public record CarrinhoDTO(List<ItemCarrinhoDTO> itens, int quantidadeItens, String total) {
}
