package br.com.paccellilanches.controller;

import jakarta.ws.rs.core.NewCookie;

/**
 * Cookie HTTP que carrega o id da sessão do usuário.
 */
public final class CookieSessao {

    public static final String NOME = "sessionId";

    private CookieSessao() {
    }

    public static NewCookie criar(String sessionId) {
        return montar(sessionId, NewCookie.DEFAULT_MAX_AGE);
    }

    public static NewCookie expirado() {
        return montar("", 0);
    }

    private static NewCookie montar(String valor, int maxAge) {
        return new NewCookie.Builder(NOME)
                .value(valor)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .build();
    }
}
