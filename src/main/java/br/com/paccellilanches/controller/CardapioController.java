package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.LancheBO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cardapio")
public class CardapioController {

    @Inject
    Template cardapio;

    @Inject
    LancheBO lancheBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return cardapio.data("categorias", lancheBO.cardapio());
    }
}
