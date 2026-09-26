package br.com.paccellilanches.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class AuditLog extends PanacheEntity {

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
