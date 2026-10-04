package br.com.paccellilanches.dto;

public record ItemCarrinhoDTO(Long lancheId, String nome, String descricao, int quantidade,
                              String precoUnitario, String subtotal) {
}
