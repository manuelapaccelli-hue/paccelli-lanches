package br.com.paccellilanches.bootstrap;

import br.com.paccellilanches.dao.TipoUsuarioDAO;
import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.entity.TipoUsuario;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * Na inicialização, cadastra os tipos de usuário e os usuários iniciais do sistema.
 * O que já existir (tipo pelo nome, usuário pelo e-mail) é ignorado.
 */
@ApplicationScoped
public class UsuarioBootstrap {

    @Inject
    TipoUsuarioDAO tipoUsuarioDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    EntityManager entityManager;

    @Transactional
    void popularDadosIniciais(@Observes StartupEvent evento) {
        TipoUsuario admin = cadastrarTipoSeNaoExistir(TipoUsuario.ADMIN, "Administrador");
        TipoUsuario comum = cadastrarTipoSeNaoExistir(TipoUsuario.COMUM, "Comum");

        migrarColunaAdmin(admin, comum);

        cadastrarUsuarioSeNaoExistir("Manuela Paccelli", "manuela@paccellilanches.com", "manuela123", admin);
        cadastrarUsuarioSeNaoExistir("Samuel Alarcão", "samuel@paccellilanches.com", "samuel123", comum);
    }

    private TipoUsuario cadastrarTipoSeNaoExistir(String nome, String descricao) {
        return tipoUsuarioDAO.buscarPorNome(nome).orElseGet(() -> {
            TipoUsuario tipo = new TipoUsuario(nome, descricao);
            tipoUsuarioDAO.salvar(tipo);
            return tipo;
        });
    }

    /**
     * Bancos criados antes da entidade TipoUsuario têm a coluna booleana "admin" na tabela usuario.
     * Converte esse valor para o tipo correspondente e remove a coluna antiga.
     * A coluna tipo_usuario_id é criada aqui porque o Hibernate não consegue adicioná-la
     * como "not null" numa tabela que já tem usuários.
     */
    private void migrarColunaAdmin(TipoUsuario admin, TipoUsuario comum) {
        Number colunas = (Number) entityManager.createNativeQuery(
                "select count(*) from information_schema.columns "
                        + "where table_name = 'usuario' and column_name = 'admin'")
                .getSingleResult();
        if (colunas.intValue() == 0) {
            return;
        }

        entityManager.createNativeQuery("alter table usuario add column if not exists tipo_usuario_id bigint "
                + "references tipo_usuario (id)").executeUpdate();
        entityManager.createNativeQuery(
                "update usuario set tipo_usuario_id = case when admin then :admin else :comum end "
                        + "where tipo_usuario_id is null")
                .setParameter("admin", admin.id)
                .setParameter("comum", comum.id)
                .executeUpdate();
        entityManager.createNativeQuery("alter table usuario drop column admin").executeUpdate();
        entityManager.createNativeQuery("alter table usuario alter column tipo_usuario_id set not null").executeUpdate();
    }

    private void cadastrarUsuarioSeNaoExistir(String nome, String email, String senha, TipoUsuario tipo) {
        if (usuarioDAO.existePorEmail(email)) {
            return;
        }

        usuarioDAO.salvar(new Usuario(nome, email, null, null, null, senha, tipo));
    }
}
