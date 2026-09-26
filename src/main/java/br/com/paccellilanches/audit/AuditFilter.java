package br.com.paccellilanches.audit;

import br.com.paccellilanches.bo.AuditoriaBO;
import br.com.paccellilanches.bo.SessaoBO;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.Provider;

/**
 * Intercepta toda requisição que chega aos endpoints da aplicação e registra
 * a ação no log de auditoria, identificando o usuário pelo cookie de sessão
 * (ver SessaoBO); sem sessão válida, é registrado como "Anônimo".
 */
@Provider
@Priority(Priorities.USER)
public class AuditFilter implements ContainerRequestFilter {

    private static final String COOKIE_SESSAO = "sessionId";

    @Inject
    AuditoriaBO auditoriaBO;

    @Inject
    SessaoBO sessaoBO;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String acao = requestContext.getMethod() + " " + requestContext.getUriInfo().getPath();
        String usuario = obterUsuario(requestContext);
        auditoriaBO.registrar(acao, usuario);
    }

    private String obterUsuario(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(COOKIE_SESSAO);
        if (cookie == null) {
            return "Anônimo";
        }
        return sessaoBO.emailDaSessao(cookie.getValue()).orElse("Anônimo");
    }
}
