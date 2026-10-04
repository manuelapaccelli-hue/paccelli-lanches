package br.com.paccellilanches.dto;

import java.math.BigDecimal;

public record LancheGestaoDTO(Long id, String nome, String descricao, BigDecimal preco,
                              String categoria, String categoriaTitulo) {

    public String precoFormatado() {
        return Moeda.formatar(preco);
    }
}
