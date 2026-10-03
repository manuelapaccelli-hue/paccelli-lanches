package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.dto.LoginDTO;
import br.com.paccellilanches.dto.SessaoStatusDTO;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Login, logout e verificação de quem está logado e do que pode acessar.
 */
@ApplicationScoped
public class AutenticacaoBO {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    SessaoBO sessaoBO;

    /** Autentica o usuário e devolve o id da sessão criada. */
    public String login(LoginDTO request) {
        boolean senhaCorreta = usuarioDAO.buscarAtivoPorEmail(request.email())
                .map(usuario -> usuario.senha.equals(request.senha()))
                .orElse(false);
        if (!senhaCorreta) {
            throw new NaoAutenticadoException("E-mail ou senha inválidos.");
        }
        return sessaoBO.criar(request.email());
    }

    public void logout(String sessionId) {
        sessaoBO.encerrar(sessionId);
    }

    public SessaoStatusDTO status(String sessionId) {
        return usuarioLogado(sessionId)
                .map(usuario -> new SessaoStatusDTO(true, usuario.email, usuario.admin))
                .orElse(new SessaoStatusDTO(false, null, false));
    }

    public Optional<Usuario> usuarioLogado(String sessionId) {
        return sessaoBO.emailDaSessao(sessionId)
                .flatMap(usuarioDAO::buscarAtivoPorEmail);
    }

    public Usuario exigirLogado(String sessionId) {
        return usuarioLogado(sessionId)
                .orElseThrow(() -> new NaoAutenticadoException("Faça login para continuar."));
    }

    public Usuario exigirAdmin(String sessionId) {
        Usuario usuario = exigirLogado(sessionId);
        if (!usuario.admin) {
            throw new AcessoNegadoException("Acesso restrito a administradores.");
        }
        return usuario;
    }
}
