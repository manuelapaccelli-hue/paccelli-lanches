package br.com.paccellilanches.controller;

import br.com.paccellilanches.bo.AcessoNegadoException;
import br.com.paccellilanches.bo.NaoAutenticadoException;
import br.com.paccellilanches.bo.NegocioException;
import br.com.paccellilanches.bo.RecursoNaoEncontradoException;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.net.URI;

/**
 * Traduz as exceções de negócio dos BOs para respostas HTTP.
 * <ul>
 *   <li>Navegação de páginas (GET que aceita HTML): sem login, redireciona para /login;
 *       sem permissão, mostra a tela de acesso negado.</li>
 *   <li>Chamadas do JavaScript (fetch/JSON): devolve o status e a mensagem em texto puro,
 *       formato que o front-end já lê com response.text().</li>
 * </ul>
 */
public class NegocioExceptionMapper {

    @Inject
    @Location("acesso-negado.html")
    Template acessoNegado;

    @ServerExceptionMapper
    public Response mapear(NegocioException exception, ContainerRequestContext requestContext) {
        if (ehNavegacaoDePagina(requestContext)) {
            if (exception instanceof NaoAutenticadoException) {
                return Response.seeOther(URI.create("/login")).build();
            }
            if (exception instanceof AcessoNegadoException) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(acessoNegado.data("mensagem", exception.getMessage()).render())
                        .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                        .build();
            }
        }
        return Response.status(status(exception))
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"))
                .build();
    }

    /** O navegador abrindo uma tela: GET que aceita HTML. O fetch do JavaScript não pede HTML. */
    private boolean ehNavegacaoDePagina(ContainerRequestContext requestContext) {
        if (!HttpMethod.GET.equals(requestContext.getMethod())) {
            return false;
        }
        String accept = requestContext.getHeaderString("Accept");
        return accept != null && accept.contains(MediaType.TEXT_HTML);
    }

    private Response.Status status(NegocioException exception) {
        if (exception instanceof NaoAutenticadoException) {
            return Response.Status.UNAUTHORIZED;
        }
        if (exception instanceof AcessoNegadoException) {
            return Response.Status.FORBIDDEN;
        }
        if (exception instanceof RecursoNaoEncontradoException) {
            return Response.Status.NOT_FOUND;
        }
        return Response.Status.BAD_REQUEST;
    }
}
