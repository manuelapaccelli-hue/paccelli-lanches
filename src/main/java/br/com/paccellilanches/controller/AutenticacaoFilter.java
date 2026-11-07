package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AutenticacaoBO;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.Provider;

import java.util.Set;

/**
 * Só deixa passar quem está logado: qualquer endpoint fora das rotas públicas exige uma
 * sessão válida. Sem sessão, o AutenticacaoBO lança NaoAutenticadoException, que o
 * NegocioExceptionMapper transforma em redirecionamento para /login (páginas) ou 401 (JSON).
 * A verificação de perfil (admin) continua nos BOs de cada recurso.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AutenticacaoFilter implements ContainerRequestFilter {

    /** Rotas acessíveis sem login: tela e API de login/logout, status da sessão e cadastro. */
    private static final Set<String> ROTAS_PUBLICAS = Set.of("login", "login/status", "login/sessao", "cadastro");

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (ROTAS_PUBLICAS.contains(normalizar(requestContext.getUriInfo().getPath()))) {
            return;
        }
        Cookie cookie = requestContext.getCookies().get(CookieSessao.NOME);
        autenticacaoBO.exigirLogado(cookie == null ? null : cookie.getValue());
    }

    /** "/login/" e "login" viram "login", para comparar com as rotas públicas. */
    private String normalizar(String caminho) {
        return caminho.replaceAll("^/+|/+$", "");
    }
}
