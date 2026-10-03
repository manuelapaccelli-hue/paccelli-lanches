package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.AuditLogDAO;
import br.com.paccellilanches.dto.AuditLogDTO;
import br.com.paccellilanches.entity.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AuditoriaBO {

    @Inject
    AuditLogDAO auditLogDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Transactional
    public void registrar(String acao, String usuario) {
        auditLogDAO.salvar(new AuditLog(acao, usuario, LocalDateTime.now()));
    }

    public List<AuditLogDTO> listarTodos(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        return auditLogDAO.listarTodos()
                .stream()
                .map(log -> new AuditLogDTO(log.dataHora, log.usuario, log.acao))
                .toList();
    }
}
