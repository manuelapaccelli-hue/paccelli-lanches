package br.com.paccellilanches.bo;

import br.com.paccellilanches.dto.SessaoStatusDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Monta as telas (templates Qute) com os dados fornecidos pelos demais BOs.
 */
@ApplicationScoped
public class PaginaBO {

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    AuditoriaBO auditoriaBO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Inject
    Template index;

    @Inject
    Template cardapio;

    @Inject
    Template contato;

    @Inject
    Template promocoes;

    @Inject
    Template login;

    @Inject
    Template cadastro;

    @Inject
    Template perfil;

    @Inject
    Template usuarios;

    @Inject
    Template auditoria;

    public TemplateInstance index() {
        return index.instance();
    }

    public TemplateInstance cardapio() {
        return cardapio.instance();
    }

    public TemplateInstance contato() {
        return contato.instance();
    }

    public TemplateInstance promocoes() {
        return promocoes.instance();
    }

    public TemplateInstance login() {
        return login.instance();
    }

    public TemplateInstance cadastro() {
        return cadastro.instance();
    }

    public TemplateInstance perfil() {
        return perfil.instance();
    }

    public TemplateInstance usuarios(String sessionId) {
        SessaoStatusDTO sessao = autenticacaoBO.status(sessionId);
        return usuarios.data("usuarios", usuarioBO.listarAtivos(sessionId))
                .data("emailLogado", sessao.email());
    }

    public TemplateInstance auditoria(String sessionId) {
        return auditoria.data("logs", auditoriaBO.listarTodos(sessionId));
    }
}
