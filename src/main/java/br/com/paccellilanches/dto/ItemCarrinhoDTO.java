package br.com.paccellilanches.dto;

/** Item do carrinho; precoOriginal só é preenchido quando o lanche está em promoção. */
public record ItemCarrinhoDTO(Long lancheId, String nome, String descricao, int quantidade,
                              String precoUnitario, String precoOriginal, String subtotal) {
}
