package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auditoria")
public class AuditoriaController {

    @Inject
    PaginaBO paginaBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return paginaBO.auditoria(sessionId);
    }
}
