package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.AdminDTO;
import br.com.paccellilanches.dto.TelefoneDTO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios")
public class UsuarioController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return paginaBO.usuarios(sessionId);
    }

    @PUT
    @Path("/{id}/admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alterarAdmin(@CookieParam(CookieSessao.NOME) String sessionId,
                                 @PathParam("id") Long id, AdminDTO request) {
        usuarioBO.alterarAdmin(sessionId, id, request);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/telefone")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alterarTelefone(@CookieParam(CookieSessao.NOME) String sessionId,
                                    @PathParam("id") Long id, TelefoneDTO request) {
        usuarioBO.alterarTelefone(sessionId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@CookieParam(CookieSessao.NOME) String sessionId,
                            @PathParam("id") Long id) {
        usuarioBO.excluir(sessionId, id);
        return Response.noContent().build();
    }
}
