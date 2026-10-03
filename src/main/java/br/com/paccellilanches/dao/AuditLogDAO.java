package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.AuditLog;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AuditLogDAO {

    public void salvar(AuditLog log) {
        log.persist();
    }

    public List<AuditLog> listarTodos() {
        return AuditLog.listAll(Sort.descending("dataHora"));
    }
}
