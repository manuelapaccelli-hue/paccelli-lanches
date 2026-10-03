package br.com.paccellilanches.bo;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Traduz as exceções de negócio dos BOs para respostas HTTP, devolvendo a
 * mensagem em texto puro (formato que o front-end já lê com response.text()).
 */
@Provider
public class NegocioExceptionMapper implements ExceptionMapper<NegocioException> {

    @Override
    public Response toResponse(NegocioException exception) {
        return Response.status(status(exception))
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"))
                .build();
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
