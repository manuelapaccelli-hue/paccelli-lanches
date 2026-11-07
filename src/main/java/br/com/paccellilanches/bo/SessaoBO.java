package br.com.paccellilanches.bo;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Guarda as sessões abertas em memória (sessionId -> e-mail do usuário).
 */
@ApplicationScoped
public class SessaoBO {

    private final Map<String, String> sessoes = new ConcurrentHashMap<>();

    public String criar(String email) {
        String sessionId = UUID.randomUUID().toString();
        sessoes.put(sessionId, email);
        return sessionId;
    }

    public Optional<String> emailDaSessao(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessoes.get(sessionId));
    }

    /** Quando o e-mail de um usuário muda, as sessões abertas dele passam a apontar para o novo e-mail. */
    public void atualizarEmail(String emailAntigo, String emailNovo) {
        sessoes.replaceAll((sessionId, email) -> email.equals(emailAntigo) ? emailNovo : email);
    }

    public void encerrar(String sessionId) {
        if (sessionId != null) {
            sessoes.remove(sessionId);
        }
    }
}
