package br.com.paccellilanches.dto;

import java.math.BigDecimal;

/** Lanche do cardápio; precoPromocional é null quando o lanche não está em promoção. */
public record LancheDTO(Long id, String nome, String descricao, BigDecimal preco, BigDecimal precoPromocional) {

    public boolean emPromocao() {
        return precoPromocional != null;
    }

    public String precoFormatado() {
        return Moeda.formatar(preco);
    }

    public String precoPromocionalFormatado() {
        return emPromocao() ? Moeda.formatar(precoPromocional) : null;
    }
}
