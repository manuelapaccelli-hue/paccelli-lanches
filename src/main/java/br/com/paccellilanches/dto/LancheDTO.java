package br.com.paccellilanches.dto;

import java.math.BigDecimal;

public record LancheDTO(Long id, String nome, String descricao, BigDecimal preco) {

    public String precoFormatado() {
        return Moeda.formatar(preco);
    }
}
