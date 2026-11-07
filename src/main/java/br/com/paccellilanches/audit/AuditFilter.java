package br.com.paccellilanches.audit;

import br.com.paccellilanches.bo.AuditoriaBO;
import br.com.paccellilanches.bo.SessaoBO;
import br.com.paccellilanches.controller.CookieSessao;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.Provider;

/**
 * Registra no log de auditoria toda requisição que chega aos endpoints da aplicação.
 * <ul>
 *   <li>Na chegada (antes do AutenticacaoFilter), guarda quem é o usuário da sessão, para que até
 *       os acessos barrados e o logout (que encerra a sessão) fiquem com o usuário certo.</li>
 *   <li>Na resposta, grava uma linha com o usuário, a ação legível, a requisição técnica,
 *       o status HTTP e a mensagem de erro, se houver (ver ContextoAuditoria).</li>
 * </ul>
 */
@Provider
// Roda antes do AutenticacaoFilter, para registrar também os acessos barrados por falta de login
@Priority(Priorities.AUTHENTICATION - 1)
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String ANONIMO = "Anônimo";

    @Inject
    AuditoriaBO auditoriaBO;

    @Inject
    SessaoBO sessaoBO;

    @Inject
    ContextoAuditoria contexto;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(CookieSessao.NOME);
        contexto.guardarUsuarioSessao(cookie == null ? null : sessaoBO.emailDaSessao(cookie.getValue()).orElse(null));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String metodo = requestContext.getMethod();
        String caminho = requestContext.getUriInfo().getPath();
        String usuario = contexto.usuario() != null ? contexto.usuario() : ANONIMO;
        String acao = contexto.descricao() != null ? contexto.descricao() : auditoriaBO.descrever(metodo, caminho);
        auditoriaBO.registrar(usuario, acao, metodo + " " + caminho, responseContext.getStatus(), contexto.erro());
    }
}
