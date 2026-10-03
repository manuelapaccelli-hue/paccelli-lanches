package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.PaginaBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.CadastroDTO;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cadastro")
public class CadastroController {

    @Inject
    PaginaBO paginaBO;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return paginaBO.cadastro();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrar(CadastroDTO request) {
        usuarioBO.cadastrar(request);
        return Response.status(Response.Status.CREATED).build();
    }
}
