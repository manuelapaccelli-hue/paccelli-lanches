package br.com.paccellilanches.bo;

/**
 * O registro solicitado não existe.
 */
public class RecursoNaoEncontradoException extends NegocioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
