package br.com.paccellilanches.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formata valores em reais (ex.: R$ 18,90) para exibição nas telas.
 */
public final class Moeda {

    private Moeda() {
    }

    public static String formatar(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.of("pt", "BR")).format(valor);
    }
}
