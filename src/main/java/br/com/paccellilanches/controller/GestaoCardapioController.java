package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.LancheBO;
import br.com.paccellilanches.dto.LancheFormDTO;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
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
 * Tela de gestão do cardápio, restrita a administradores.
 */
@Path("/gestao-cardapio")
public class GestaoCardapioController {

    @Inject
    @Location("gestao-cardapio.html")
    Template gestaoCardapio;

    @Inject
    LancheBO lancheBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return gestaoCardapio.data("lanches", lancheBO.listarParaGestao(sessionId))
                .data("categorias", lancheBO.categorias());
    }

    @POST
    @Path("/lanches")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastrar(@CookieParam(CookieSessao.NOME) String sessionId, LancheFormDTO request) {
        lancheBO.cadastrar(sessionId, request);
        return Response.noContent().build();
    }

    @PUT
    @Path("/lanches/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editar(@CookieParam(CookieSessao.NOME) String sessionId,
                           @PathParam("id") Long id, LancheFormDTO request) {
        lancheBO.editar(sessionId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/lanches/{id}")
    public Response remover(@CookieParam(CookieSessao.NOME) String sessionId, @PathParam("id") Long id) {
        lancheBO.remover(sessionId, id);
        return Response.noContent().build();
    }
}
