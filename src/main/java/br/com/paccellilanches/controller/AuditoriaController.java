package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AuditoriaBO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auditoria")
public class AuditoriaController {

    @Inject
    Template auditoria;

    @Inject
    AuditoriaBO auditoriaBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return auditoria.data("logs", auditoriaBO.listarTodos());
    }
}
