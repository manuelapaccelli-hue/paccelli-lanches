package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.AuditLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AuditLogDAO implements PanacheRepository<AuditLog> {

    public void salvar(AuditLog log) {
        persist(log);
    }

    public List<AuditLog> listarTodos() {
        return listAll(Sort.descending("dataHora"));
    }
}
