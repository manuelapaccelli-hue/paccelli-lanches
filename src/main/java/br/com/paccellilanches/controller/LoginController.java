package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AutenticacaoBO;
import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.dto.LoginDTO;
import br.com.paccellilanches.dto.SessaoStatusDTO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return paginaBO.login();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticar(LoginDTO request) {
        String sessionId = autenticacaoBO.login(request);
        return Response.ok().cookie(CookieSessao.criar(sessionId)).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public SessaoStatusDTO status(@CookieParam(CookieSessao.NOME) String sessionId) {
        return autenticacaoBO.status(sessionId);
    }

    @DELETE
    @Path("/sessao")
    public Response logout(@CookieParam(CookieSessao.NOME) String sessionId) {
        autenticacaoBO.logout(sessionId);
        return Response.ok().cookie(CookieSessao.expirado()).build();
    }
}
