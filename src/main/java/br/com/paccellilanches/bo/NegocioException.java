package br.com.paccellilanches.bo;

/**
 * Erro de regra de negócio lançado pelos BOs. A conversão para resposta HTTP
 * fica a cargo do NegocioExceptionMapper.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
