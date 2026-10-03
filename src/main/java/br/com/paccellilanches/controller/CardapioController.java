package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cardapio")
public class CardapioController {

    @Inject
    PaginaBO paginaBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return paginaBO.cardapio();
    }
}
