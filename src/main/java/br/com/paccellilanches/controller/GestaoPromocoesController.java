package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.bo.PromocaoBO;
import br.com.paccellilanches.dto.PromocaoFormDTO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Tela de gestão das promoções, restrita a administradores.
 */
@Path("/gestao-promocoes")
public class GestaoPromocoesController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    PromocaoBO promocaoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return paginaBO.gestaoPromocoes(sessionId);
    }

    @POST
    @Path("/promocoes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastrar(@CookieParam(CookieSessao.NOME) String sessionId, PromocaoFormDTO request) {
        promocaoBO.cadastrar(sessionId, request);
        return Response.noContent().build();
    }

    @PUT
    @Path("/promocoes/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editar(@CookieParam(CookieSessao.NOME) String sessionId,
                           @PathParam("id") Long id, PromocaoFormDTO request) {
        promocaoBO.editar(sessionId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/promocoes/{id}")
    public Response encerrar(@CookieParam(CookieSessao.NOME) String sessionId, @PathParam("id") Long id) {
        promocaoBO.encerrar(sessionId, id);
        return Response.noContent().build();
    }
}
