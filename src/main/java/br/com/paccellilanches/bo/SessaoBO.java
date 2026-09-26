package br.com.paccellilanches.bo;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    public void encerrar(String sessionId) {
        if (sessionId != null) {
            sessoes.remove(sessionId);
        }
    }
}
