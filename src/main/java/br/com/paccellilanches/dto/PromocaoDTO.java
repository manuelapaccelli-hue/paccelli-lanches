package br.com.paccellilanches.dto;

public record PromocaoDTO(Long lancheId, String nome, String descricao, String selo, String precoOriginal,
                          String precoPromocional, String economia, String validaAte) {
}
