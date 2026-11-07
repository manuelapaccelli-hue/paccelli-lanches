package br.com.paccellilanches.dto;

import java.time.LocalDate;

/**
 * Formulário de cadastro/edição de usuário pelo administrador. Na edição, a senha é opcional:
 * em branco, a senha atual é mantida.
 */
public record UsuarioFormDTO(String nome, String email, String telefone, String cpf, LocalDate dataNascimento,
                             String tipo, String senha) {
}
