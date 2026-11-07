package br.com.paccellilanches.dto;

import java.time.LocalDate;

/** Dados que o próprio usuário pode alterar na tela de perfil. */
public record PerfilFormDTO(String nome, String email, String telefone, String cpf, LocalDate dataNascimento) {
}
