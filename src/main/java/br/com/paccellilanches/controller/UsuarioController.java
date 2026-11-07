package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AutenticacaoBO;
import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.UsuarioFormDTO;
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
 * Tela de gestão de usuários, restrita a administradores: cadastrar, editar e excluir.
 */
@Path("/usuarios")
public class UsuarioController {

    @Inject
    Template usuarios;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return usuarios.data("usuarios", usuarioBO.listarAtivos(sessionId))
                .data("tipos", usuarioBO.listarTipos())
                .data("emailLogado", autenticacaoBO.status(sessionId).email());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastrar(@CookieParam(CookieSessao.NOME) String sessionId, UsuarioFormDTO request) {
        usuarioBO.cadastrarPeloAdmin(sessionId, request);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editar(@CookieParam(CookieSessao.NOME) String sessionId,
                           @PathParam("id") Long id, UsuarioFormDTO request) {
        usuarioBO.editarPeloAdmin(sessionId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@CookieParam(CookieSessao.NOME) String sessionId,
                            @PathParam("id") Long id) {
        usuarioBO.excluir(sessionId, id);
        return Response.noContent().build();
    }
}
