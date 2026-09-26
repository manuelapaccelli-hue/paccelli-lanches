package br.com.paccellilanches.dto;

import java.time.LocalDate;

public record CadastroDTO(String nome, String email, String telefone, String cpf,
                           LocalDate dataNascimento, String senha) {
}
