package br.com.paccellilanches.bo;

/**
 * Usuário autenticado, mas sem permissão para a operação.
 */
public class AcessoNegadoException extends NegocioException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
