package br.com.paccellilanches.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public record LancheDTO(Long id, String nome, String descricao, BigDecimal preco) {

    public String precoFormatado() {
        return NumberFormat.getCurrencyInstance(Locale.of("pt", "BR")).format(preco);
    }
}
