package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.SessaoBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.PerfilDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/perfil")
public class PerfilController {

    private static final String COOKIE_SESSAO = "sessionId";

    @Inject
    Template perfil;

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    SessaoBO sessaoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return perfil.instance();
    }

    @GET
    @Path("/dados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dados(@CookieParam(COOKIE_SESSAO) String sessionId) {
        return sessaoBO.emailDaSessao(sessionId)
                .flatMap(usuarioBO::buscarPerfil)
                .map(perfil -> Response.ok(perfil).build())
                .orElse(Response.status(Response.Status.UNAUTHORIZED).build());
    }
}
