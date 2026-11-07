package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PedidoBO;
import br.com.paccellilanches.dto.PedidoDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Pedidos do cliente: finalizar a compra do carrinho e a tela "Meus pedidos".
 */
@Path("/pedidos")
public class PedidoController {

    @Inject
    Template pedidos;

    @Inject
    PedidoBO pedidoBO;

    /** Tela "Meus pedidos"; ?novo={id} destaca o pedido que acabou de ser feito. */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId, @QueryParam("novo") Long novo) {
        return pedidos.data("pedidos", pedidoBO.meusPedidos(sessionId))
                .data("pedidoNovo", novo);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response finalizar(@CookieParam(CookieSessao.NOME) String sessionId) {
        PedidoDTO pedido = pedidoBO.finalizar(sessionId);
        return Response.status(Response.Status.CREATED).entity(pedido).build();
    }
}
