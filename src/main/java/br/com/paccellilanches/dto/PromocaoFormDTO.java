package br.com.paccellilanches.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromocaoFormDTO(Long lancheId, BigDecimal precoPromocional, String selo,
                              LocalDate dataInicio, LocalDate dataFim) {
}
