package br.com.paccellilanches.dto;

import java.math.BigDecimal;

public record LancheFormDTO(String nome, String descricao, BigDecimal preco, String categoria) {
}
