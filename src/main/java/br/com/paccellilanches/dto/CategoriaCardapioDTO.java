package br.com.paccellilanches.dto;

import java.util.List;

public record CategoriaCardapioDTO(String titulo, List<LancheDTO> lanches) {
}
