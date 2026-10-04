package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.CarrinhoBO;
import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.dto.AdicionarItemDTO;
import br.com.paccellilanches.dto.CarrinhoDTO;
import br.com.paccellilanches.dto.QuantidadeDTO;
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

@Path("/carrinho")
public class CarrinhoController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    CarrinhoBO carrinhoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return paginaBO.carrinho();
    }

    @GET
    @Path("/itens")
    @Produces(MediaType.APPLICATION_JSON)
    public CarrinhoDTO itens(@CookieParam(CookieSessao.NOME) String sessionId) {
        return carrinhoBO.carrinho(sessionId);
    }

    @POST
    @Path("/itens")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adicionar(@CookieParam(CookieSessao.NOME) String sessionId, AdicionarItemDTO request) {
        carrinhoBO.adicionar(sessionId, request);
        return Response.noContent().build();
    }

    @PUT
    @Path("/itens/{lancheId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alterarQuantidade(@CookieParam(CookieSessao.NOME) String sessionId,
                                      @PathParam("lancheId") Long lancheId, QuantidadeDTO request) {
        carrinhoBO.alterarQuantidade(sessionId, lancheId, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/itens/{lancheId}")
    public Response remover(@CookieParam(CookieSessao.NOME) String sessionId,
                            @PathParam("lancheId") Long lancheId) {
        carrinhoBO.remover(sessionId, lancheId);
        return Response.noContent().build();
    }
}
