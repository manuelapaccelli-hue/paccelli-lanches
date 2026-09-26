package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AuditoriaBO;
import br.com.paccellilanches.bo.SessaoBO;
import br.com.paccellilanches.bo.UsuarioBO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auditoria")
public class AuditoriaController {

    private static final String COOKIE_SESSAO = "sessionId";

    @Inject
    Template auditoria;

    @Inject
    AuditoriaBO auditoriaBO;

    @Inject
    SessaoBO sessaoBO;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(COOKIE_SESSAO) String sessionId) {
        String email = sessaoBO.emailDaSessao(sessionId).orElse(null);
        if (!usuarioBO.isAdmin(email)) {
            throw new ForbiddenException("Acesso restrito a administradores.");
        }
        return auditoria.data("logs", auditoriaBO.listarTodos());
    }
}