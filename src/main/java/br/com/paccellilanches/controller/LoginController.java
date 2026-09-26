package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.SessaoBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.LoginDTO;
import br.com.paccellilanches.dto.SessaoStatusDTO;
import io.quarkus.qute.Template;
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
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginController {

    private static final String COOKIE_SESSAO = "sessionId";

    @Inject
    Template login;

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    SessaoBO sessaoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return login.instance();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticar(LoginDTO request) {
        if (!usuarioBO.autenticar(request)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("E-mail ou senha inválidos.")
                    .build();
        }

        String sessionId = sessaoBO.criar(request.email());
        NewCookie cookie = new NewCookie.Builder(COOKIE_SESSAO)
                .value(sessionId)
                .path("/")
                .httpOnly(true)
                .build();

        return Response.ok().cookie(cookie).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public SessaoStatusDTO status(@CookieParam(COOKIE_SESSAO) String sessionId) {
        return sessaoBO.emailDaSessao(sessionId)
                .map(email -> new SessaoStatusDTO(true, email))
                .orElse(new SessaoStatusDTO(false, null));
    }

    @DELETE
    @Path("/sessao")
    public Response logout(@CookieParam(COOKIE_SESSAO) String sessionId) {
        sessaoBO.encerrar(sessionId);

        NewCookie cookie = new NewCookie.Builder(COOKIE_SESSAO)
                .value("")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        return Response.ok().cookie(cookie).build();
    }
}
