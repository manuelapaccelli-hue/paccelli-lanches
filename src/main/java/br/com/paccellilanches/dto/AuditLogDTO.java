package br.com.paccellilanches.dto;

/**
 * Linha da tela de auditoria, já formatada: data/hora em dd/MM/yyyy HH:mm:ss e o resultado
 * traduzido do status HTTP (resultadoCss vira a cor da etiqueta).
 */
public record AuditLogDTO(String dataHora, String usuario, String acao, String requisicao, String resultado,
                          String resultadoCss, String detalhe) {
}
