package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.TipoUsuarioDAO;
import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.dto.AlterarTipoDTO;
import br.com.paccellilanches.dto.CadastroDTO;
import br.com.paccellilanches.dto.PerfilDTO;
import br.com.paccellilanches.dto.TelefoneDTO;
import br.com.paccellilanches.dto.TipoUsuarioDTO;
import br.com.paccellilanches.dto.UsuarioResumoDTO;
import br.com.paccellilanches.entity.TipoUsuario;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Regras de negócio do usuário: cadastro, perfil e gerenciamento pelos administradores.
 */
@ApplicationScoped
public class UsuarioBO {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    TipoUsuarioDAO tipoUsuarioDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Transactional
    public void cadastrar(CadastroDTO request) {
        if (usuarioDAO.existePorEmail(request.email())) {
            throw new NegocioException("Já existe uma conta cadastrada com este e-mail.");
        }
        if (request.senha() == null || request.senha().length() < 6) {
            throw new NegocioException("A senha deve ter pelo menos 6 caracteres.");
        }

        usuarioDAO.salvar(new Usuario(request.nome(), request.email(), request.telefone(),
                request.cpf(), request.dataNascimento(), request.senha(), buscarTipo(TipoUsuario.COMUM)));
    }

    public PerfilDTO perfil(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        return new PerfilDTO(usuario.nome, usuario.email, usuario.telefone,
                usuario.cpf, usuario.dataNascimento);
    }

    public List<UsuarioResumoDTO> listarAtivos(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        return usuarioDAO.listarAtivos()
                .stream()
                .map(usuario -> new UsuarioResumoDTO(usuario.id, usuario.nome, usuario.email,
                        usuario.telefone, usuario.tipo.nome))
                .toList();
    }

    public List<TipoUsuarioDTO> listarTipos() {
        return tipoUsuarioDAO.listarTodos()
                .stream()
                .map(tipo -> new TipoUsuarioDTO(tipo.nome, tipo.descricao))
                .toList();
    }

    @Transactional
    public void alterarTipo(String sessionId, Long id, AlterarTipoDTO request) {
        Usuario logado = autenticacaoBO.exigirAdmin(sessionId);
        Usuario usuario = buscarAtivo(id);
        TipoUsuario tipo = tipoUsuarioDAO.buscarPorNome(request.tipo())
                .orElseThrow(() -> new NegocioException("Tipo de usuário inválido."));

        if (usuario.id.equals(logado.id) && !tipo.ehAdmin()) {
            throw new NegocioException("Você não pode remover a sua própria permissão de administrador.");
        }

        usuario.tipo = tipo;
    }

    @Transactional
    public void alterarTelefone(String sessionId, Long id, TelefoneDTO request) {
        autenticacaoBO.exigirAdmin(sessionId);
        Usuario usuario = buscarAtivo(id);

        String telefone = request.telefone() == null ? "" : request.telefone().trim();
        if (telefone.isEmpty()) {
            throw new NegocioException("Informe um telefone.");
        }

        usuario.telefone = telefone;
    }

    /** Exclusão lógica: o usuário é desativado e volta a ser do tipo comum. */
    @Transactional
    public void excluir(String sessionId, Long id) {
        Usuario logado = autenticacaoBO.exigirAdmin(sessionId);
        Usuario usuario = buscarAtivo(id);

        if (usuario.id.equals(logado.id)) {
            throw new NegocioException("Você não pode excluir o seu próprio usuário.");
        }

        usuario.ativo = false;
        usuario.tipo = buscarTipo(TipoUsuario.COMUM);
    }

    private TipoUsuario buscarTipo(String nome) {
        return tipoUsuarioDAO.buscarPorNome(nome)
                .orElseThrow(() -> new IllegalStateException("Tipo de usuário " + nome + " não cadastrado."));
    }

    private Usuario buscarAtivo(Long id) {
        return usuarioDAO.buscarAtivoPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
