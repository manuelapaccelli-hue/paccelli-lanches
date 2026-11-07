package br.com.paccellilanches.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Uma linha do log de auditoria: quem fez o quê, quando e com qual resultado.
 */
@Entity
public class AuditLog {

    @Id
    @GeneratedValue
    public Long id;

    /** Ação em linguagem natural (ex.: "Adicionou X-Tudo ao carrinho"). */
    public String acao;

    /** E-mail do usuário, ou "Anônimo" quando não há sessão. */
    public String usuario;

    public LocalDateTime dataHora;

    /** Requisição técnica que originou a ação (ex.: "POST /carrinho/itens"). */
    public String requisicao;

    /** Status HTTP da resposta (200, 400, 403...). */
    public Integer status;

    /** Mensagem de erro, quando a ação foi recusada. */
    public String detalhe;

    public AuditLog() {
    }

    public AuditLog(String usuario, String acao, String requisicao, Integer status, String detalhe,
                    LocalDateTime dataHora) {
        this.usuario = usuario;
        this.acao = acao;
        this.requisicao = requisicao;
        this.status = status;
        this.detalhe = detalhe;
        this.dataHora = dataHora;
    }
}
