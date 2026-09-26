package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.dto.CadastroDTO;
import br.com.paccellilanches.dto.LoginDTO;
import br.com.paccellilanches.dto.PerfilDTO;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class UsuarioBO {

    @Inject
    UsuarioDAO usuarioDAO;

    public boolean autenticar(LoginDTO request) {
        return usuarioDAO.buscarPorEmail(request.email())
                .map(usuario -> usuario.senha.equals(request.senha()))
                .orElse(false);
    }

    public String cadastrar(CadastroDTO request) {
        if (usuarioDAO.existePorEmail(request.email())) {
            return "Já existe uma conta cadastrada com este e-mail.";
        }

        if (request.senha() == null || request.senha().length() < 6) {
            return "A senha deve ter pelo menos 6 caracteres.";
        }

        Usuario usuario = new Usuario(request.nome(), request.email(), request.telefone(),
                request.cpf(), request.dataNascimento(), request.senha());
        usuarioDAO.salvar(usuario);
        return null;
    }

    public Optional<PerfilDTO> buscarPerfil(String email) {
        return usuarioDAO.buscarPorEmail(email)
                .map(usuario -> new PerfilDTO(usuario.nome, usuario.email, usuario.telefone,
                        usuario.cpf, usuario.dataNascimento));
    }

    public boolean isAdmin(String email) {
        if (email == null) {
            return false;
        }
        return usuarioDAO.buscarPorEmail(email)
                .map(usuario -> usuario.admin)
                .orElse(false);
    }
}
