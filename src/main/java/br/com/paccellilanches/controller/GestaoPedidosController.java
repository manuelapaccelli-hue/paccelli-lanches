package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PedidoBO;
import br.com.paccellilanches.dto.AlterarStatusPedidoDTO;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Tela de gestão dos pedidos, restrita a administradores.
 */
@Path("/gestao-pedidos")
public class GestaoPedidosController {

    @Inject
    @Location("gestao-pedidos.html")
    Template gestaoPedidos;

    @Inject
    PedidoBO pedidoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return gestaoPedidos.data("pedidos", pedidoBO.listarParaGestao(sessionId))
                .data("status", pedidoBO.statusDisponiveis());
    }

    @PUT
    @Path("/pedidos/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alterarStatus(@CookieParam(CookieSessao.NOME) String sessionId,
                                  @PathParam("id") Long id, AlterarStatusPedidoDTO request) {
        pedidoBO.alterarStatus(sessionId, id, request);
        return Response.noContent().build();
    }
}
