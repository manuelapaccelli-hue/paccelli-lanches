package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PromocaoBO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/promocoes")
public class PromocoesController {

    @Inject
    Template promocoes;

    @Inject
    PromocaoBO promocaoBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return promocoes.data("promocoes", promocaoBO.vigentes());
    }
}
