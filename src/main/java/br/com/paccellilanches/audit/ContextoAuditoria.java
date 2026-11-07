package br.com.paccellilanches.audit;

import jakarta.enterprise.context.RequestScoped;

/**
 * O que a requisição atual quer registrar no log, além do que o AuditFilter já sabe sozinho
 * (método, caminho e status). Vive só durante uma requisição: os BOs preenchem a descrição detalhada
 * da ação (ex.: "Adicionou X-Tudo ao carrinho"), o login informa o e-mail de quem tentou entrar
 * e o NegocioExceptionMapper guarda a mensagem de erro. No fim, o AuditFilter grava tudo numa linha.
 * <p>
 * Os campos são privados e acessados só por métodos: quem injeta um bean @RequestScoped recebe um
 * proxy, e só as chamadas de método chegam à instância da requisição atual.
 */
@RequestScoped
public class ContextoAuditoria {

    private String usuarioSessao;

    private String usuario;

    private String descricao;

    private String erro;

    /** E-mail da sessão no início da requisição (antes de um logout, por exemplo, encerrá-la). */
    public void guardarUsuarioSessao(String usuarioSessao) {
        this.usuarioSessao = usuarioSessao;
    }

    public void descrever(String descricao) {
        this.descricao = descricao;
    }

    public void identificarUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void registrarErro(String erro) {
        this.erro = erro;
    }

    /** Usuário informado explicitamente (login, criação de conta) ou, senão, o da sessão; null se anônimo. */
    public String usuario() {
        return usuario != null ? usuario : usuarioSessao;
    }

    public String descricao() {
        return descricao;
    }

    public String erro() {
        return erro;
    }
}
