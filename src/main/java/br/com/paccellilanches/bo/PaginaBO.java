package br.com.paccellilanches.bo;

import br.com.paccellilanches.dto.SessaoStatusDTO;
import io.quarkus.qute.Location;
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
    LancheBO lancheBO;

    @Inject
    PromocaoBO promocaoBO;

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

    @Inject
    Template carrinho;

    @Inject
    @Location("gestao-cardapio.html")
    Template gestaoCardapio;

    @Inject
    @Location("gestao-promocoes.html")
    Template gestaoPromocoes;

    public TemplateInstance index() {
        return index.instance();
    }

    public TemplateInstance cardapio() {
        return cardapio.data("categorias", lancheBO.cardapio());
    }

    public TemplateInstance contato() {
        return contato.instance();
    }

    public TemplateInstance promocoes() {
        return promocoes.data("promocoes", promocaoBO.vigentes());
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

    public TemplateInstance carrinho() {
        return carrinho.instance();
    }

    public TemplateInstance usuarios(String sessionId) {
        SessaoStatusDTO sessao = autenticacaoBO.status(sessionId);
        return usuarios.data("usuarios", usuarioBO.listarAtivos(sessionId))
                .data("tipos", usuarioBO.listarTipos())
                .data("emailLogado", sessao.email());
    }

    public TemplateInstance auditoria(String sessionId) {
        return auditoria.data("logs", auditoriaBO.listarTodos(sessionId));
    }

    public TemplateInstance gestaoCardapio(String sessionId) {
        return gestaoCardapio.data("lanches", lancheBO.listarParaGestao(sessionId))
                .data("categorias", lancheBO.categorias());
    }

    public TemplateInstance gestaoPromocoes(String sessionId) {
        return gestaoPromocoes.data("promocoes", promocaoBO.listarParaGestao(sessionId))
                .data("lanches", lancheBO.listarParaGestao(sessionId));
    }
}
