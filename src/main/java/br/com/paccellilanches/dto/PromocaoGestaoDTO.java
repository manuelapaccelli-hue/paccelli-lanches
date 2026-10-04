package br.com.paccellilanches.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record PromocaoGestaoDTO(Long id, Long lancheId, String lancheNome, BigDecimal precoOriginal,
                                BigDecimal precoPromocional, String selo, LocalDate dataInicio,
                                LocalDate dataFim, String situacao) {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String precoOriginalFormatado() {
        return Moeda.formatar(precoOriginal);
    }

    public String precoPromocionalFormatado() {
        return Moeda.formatar(precoPromocional);
    }

    /** Situação em formato de classe CSS (ex.: "Sem desconto" vira "sem-desconto"). */
    public String situacaoCss() {
        return situacao.toLowerCase().replace(' ', '-');
    }

    public String periodo() {
        return dataInicio.format(DATA) + " a " + dataFim.format(DATA);
    }
}
