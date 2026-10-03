package br.com.paccellilanches.bo;

/**
 * Usuário não autenticado (sessão inexistente ou credenciais inválidas).
 */
public class NaoAutenticadoException extends NegocioException {

    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
