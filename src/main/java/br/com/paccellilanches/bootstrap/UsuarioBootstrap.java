package br.com.paccellilanches.bootstrap;

import br.com.paccellilanches.bo.SenhaBO;
import br.com.paccellilanches.dao.TipoUsuarioDAO;
import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.entity.TipoUsuario;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Na inicialização, cadastra os tipos de usuário e os usuários iniciais do sistema (senhas gravadas com hash).
 * O que já existir (tipo pelo nome, usuário pelo e-mail) é ignorado.
 */
@ApplicationScoped
public class UsuarioBootstrap {

    @Inject
    TipoUsuarioDAO tipoUsuarioDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    SenhaBO senhaBO;

    @Transactional
    void popularDadosIniciais(@Observes StartupEvent evento) {
        TipoUsuario admin = cadastrarTipoSeNaoExistir(TipoUsuario.ADMIN, "Administrador");
        TipoUsuario comum = cadastrarTipoSeNaoExistir(TipoUsuario.COMUM, "Comum");

        // Usuários para testar os dois perfis: um administrador e um cliente comum
        cadastrarUsuarioSeNaoExistir("Administrador Paccelli", "admin@paccellilanches.com", "admin123", admin);
        cadastrarUsuarioSeNaoExistir("Cliente Teste", "cliente@paccellilanches.com", "cliente123", comum);
    }

    private TipoUsuario cadastrarTipoSeNaoExistir(String nome, String descricao) {
        return tipoUsuarioDAO.buscarPorNome(nome).orElseGet(() -> {
            TipoUsuario tipo = new TipoUsuario(nome, descricao);
            tipoUsuarioDAO.salvar(tipo);
            return tipo;
        });
    }

    private void cadastrarUsuarioSeNaoExistir(String nome, String email, String senha, TipoUsuario tipo) {
        if (usuarioDAO.existePorEmail(email)) {
            return;
        }
        usuarioDAO.salvar(new Usuario(nome, email, null, null, null, senhaBO.gerarHash(senha), tipo));
    }
}
