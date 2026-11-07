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

    /** As ações mais recentes, da última para a primeira. */
    public List<AuditLog> listarRecentes(int quantidade) {
        return findAll(Sort.descending("dataHora")).page(0, quantidade).list();
    }
}
