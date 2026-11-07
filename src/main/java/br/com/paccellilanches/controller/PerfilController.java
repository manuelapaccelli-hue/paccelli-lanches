package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.UsuarioBO;
import br.com.paccellilanches.dto.AlterarSenhaDTO;
import br.com.paccellilanches.dto.PerfilFormDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Perfil do usuário logado: ver e editar os próprios dados e trocar a senha.
 */
@Path("/perfil")
public class PerfilController {

    @Inject
    Template perfil;

    @Inject
    UsuarioBO usuarioBO;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@CookieParam(CookieSessao.NOME) String sessionId) {
        return perfil.data("perfil", usuarioBO.perfil(sessionId));
    }

    @PUT
    @Path("/dados")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizar(@CookieParam(CookieSessao.NOME) String sessionId, PerfilFormDTO request) {
        usuarioBO.atualizarPerfil(sessionId, request);
        return Response.noContent().build();
    }

    @PUT
    @Path("/senha")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alterarSenha(@CookieParam(CookieSessao.NOME) String sessionId, AlterarSenhaDTO request) {
        usuarioBO.alterarSenha(sessionId, request);
        return Response.noContent().build();
    }
}
