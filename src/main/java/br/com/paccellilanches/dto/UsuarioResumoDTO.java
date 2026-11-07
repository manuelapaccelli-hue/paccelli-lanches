package br.com.paccellilanches.dto;

import java.time.LocalDate;

/** Linha da tela de usuários: dados exibidos e usados para preencher o formulário de edição. */
public record UsuarioResumoDTO(Long id, String nome, String email, String telefone, String cpf,
                               LocalDate dataNascimento, String tipo, String tipoDescricao) {
}
