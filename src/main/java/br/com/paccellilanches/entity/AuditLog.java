package br.com.paccellilanches.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue
    public Long id;

    public String acao;
    public String usuario;
    public LocalDateTime dataHora;

    public AuditLog() {
    }

    public AuditLog(String acao, String usuario, LocalDateTime dataHora) {
        this.acao = acao;
        this.usuario = usuario;
        this.dataHora = dataHora;
    }
}
