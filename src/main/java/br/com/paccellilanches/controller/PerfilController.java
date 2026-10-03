package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.PerfilDTO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/perfil")
public class PerfilController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return paginaBO.perfil();
    }

    @GET
    @Path("/dados")
    @Produces(MediaType.APPLICATION_JSON)
    public PerfilDTO dados(@CookieParam(CookieSessao.NOME) String sessionId) {
        return usuarioBO.perfil(sessionId);
    }
}
