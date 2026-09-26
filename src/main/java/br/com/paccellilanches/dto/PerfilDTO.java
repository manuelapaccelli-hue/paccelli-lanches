package br.com.paccellilanches.dto;

import java.time.LocalDate;

public record PerfilDTO(String nome, String email, String telefone, String cpf, LocalDate dataNascimento) {
}
