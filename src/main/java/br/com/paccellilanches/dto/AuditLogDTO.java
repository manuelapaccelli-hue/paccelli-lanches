package br.com.paccellilanches.dto;

import java.time.LocalDateTime;

public record AuditLogDTO(LocalDateTime dataHora, String usuario, String acao) {
}
