package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.TipoUsuarioDAO;
import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.dto.AlterarSenhaDTO;
import br.com.paccellilanches.dto.CadastroDTO;
import br.com.paccellilanches.dto.PerfilDTO;
import br.com.paccellilanches.dto.PerfilFormDTO;
import br.com.paccellilanches.dto.TipoUsuarioDTO;
import br.com.paccellilanches.dto.UsuarioFormDTO;
import br.com.paccellilanches.dto.UsuarioResumoDTO;
import br.com.paccellilanches.entity.TipoUsuario;
import br.com.paccellilanches.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio do usuário: autocadastro, perfil (dados e senha) e gerenciamento pelos administradores.
 */
@ApplicationScoped
public class UsuarioBO {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    TipoUsuarioDAO tipoUsuarioDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Inject
    SessaoBO sessaoBO;

    // ----- Autocadastro (tela /cadastro) -----

    @Transactional
    public void cadastrar(CadastroDTO request) {
        String email = texto(request.email());
        validarDados(request.nome(), email, null);
        validarSenha(request.senha());
        usuarioDAO.salvar(new Usuario(texto(request.nome()), email, texto(request.telefone()),
                texto(request.cpf()), request.dataNascimento(), request.senha(), buscarTipo(TipoUsuario.COMUM)));
    }

    // ----- Perfil do próprio usuário (tela /perfil) -----

    public PerfilDTO perfil(String sessionId) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        return new PerfilDTO(usuario.nome, usuario.email, usuario.telefone,
                usuario.cpf, usuario.dataNascimento);
    }

    @Transactional
    public void atualizarPerfil(String sessionId, PerfilFormDTO request) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        String email = texto(request.email());
        validarDados(request.nome(), email, usuario.id);
        aplicarDados(usuario, request.nome(), email, request.telefone(), request.cpf(), request.dataNascimento());
    }

    /** Troca de senha pelo próprio usuário: exige a senha atual. */
    @Transactional
    public void alterarSenha(String sessionId, AlterarSenhaDTO request) {
        Usuario usuario = autenticacaoBO.exigirLogado(sessionId);
        if (request.senhaAtual() == null || !usuario.senha.equals(request.senhaAtual())) {
            throw new NegocioException("A senha atual está incorreta.");
        }
        validarSenha(request.novaSenha());
        if (request.novaSenha().equals(request.senhaAtual())) {
            throw new NegocioException("A nova senha deve ser diferente da senha atual.");
        }
        usuario.senha = request.novaSenha();
    }

    // ----- Gerenciamento pelo administrador (tela /usuarios) -----

    public List<UsuarioResumoDTO> listarAtivos(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        return usuarioDAO.listarAtivos()
                .stream()
                .map(usuario -> new UsuarioResumoDTO(usuario.id, usuario.nome, usuario.email, usuario.telefone,
                        usuario.cpf, usuario.dataNascimento, usuario.tipo.nome, usuario.tipo.descricao))
                .toList();
    }

    public List<TipoUsuarioDTO> listarTipos() {
        return tipoUsuarioDAO.listarTodos()
                .stream()
                .map(tipo -> new TipoUsuarioDTO(tipo.nome, tipo.descricao))
                .toList();
    }

    /** O administrador cadastra um usuário já escolhendo o tipo (ADMIN ou COMUM). */
    @Transactional
    public void cadastrarPeloAdmin(String sessionId, UsuarioFormDTO request) {
        autenticacaoBO.exigirAdmin(sessionId);
        String email = texto(request.email());
        validarDados(request.nome(), email, null);
        validarSenha(request.senha());
        TipoUsuario tipo = buscarTipoInformado(request.tipo());
        usuarioDAO.salvar(new Usuario(texto(request.nome()), email, texto(request.telefone()),
                texto(request.cpf()), request.dataNascimento(), request.senha(), tipo));
    }

    /**
     * O administrador edita os dados de um usuário. A senha só muda se for informada, e o
     * administrador não pode tirar a própria permissão de administrador.
     */
    @Transactional
    public void editarPeloAdmin(String sessionId, Long id, UsuarioFormDTO request) {
        Usuario logado = autenticacaoBO.exigirAdmin(sessionId);
        Usuario usuario = buscarAtivo(id);
        String email = texto(request.email());
        validarDados(request.nome(), email, usuario.id);
        TipoUsuario tipo = buscarTipoInformado(request.tipo());
        if (usuario.id.equals(logado.id) && !tipo.ehAdmin()) {
            throw new NegocioException("Você não pode remover a sua própria permissão de administrador.");
        }
        if (texto(request.senha()) != null) {
            validarSenha(request.senha());
            usuario.senha = request.senha();
        }
        aplicarDados(usuario, request.nome(), email, request.telefone(), request.cpf(), request.dataNascimento());
        usuario.tipo = tipo;
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

    // ----- Regras compartilhadas -----

    /** Nome e e-mail obrigatórios, e o e-mail não pode ser de outro usuário (idAtual = null no cadastro). */
    private void validarDados(String nome, String email, Long idAtual) {
        if (texto(nome) == null) {
            throw new NegocioException("Informe o nome.");
        }
        if (email == null) {
            throw new NegocioException("Informe o e-mail.");
        }
        boolean emailEmUso = idAtual == null
                ? usuarioDAO.existePorEmail(email)
                : usuarioDAO.existeOutroComEmail(email, idAtual);
        if (emailEmUso) {
            throw new NegocioException("Já existe uma conta cadastrada com este e-mail.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new NegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

    /** Copia os dados validados para o usuário; se o e-mail mudou, as sessões abertas acompanham. */
    private void aplicarDados(Usuario usuario, String nome, String email, String telefone, String cpf,
                              LocalDate dataNascimento) {
        if (!email.equals(usuario.email)) {
            sessaoBO.atualizarEmail(usuario.email, email);
        }
        usuario.nome = texto(nome);
        usuario.email = email;
        usuario.telefone = texto(telefone);
        usuario.cpf = texto(cpf);
        usuario.dataNascimento = dataNascimento;
    }

    private TipoUsuario buscarTipoInformado(String nome) {
        if (texto(nome) == null) {
            throw new NegocioException("Escolha o tipo de usuário.");
        }
        return tipoUsuarioDAO.buscarPorNome(nome.trim())
                .orElseThrow(() -> new NegocioException("Tipo de usuário inválido."));
    }

    private TipoUsuario buscarTipo(String nome) {
        return tipoUsuarioDAO.buscarPorNome(nome)
                .orElseThrow(() -> new IllegalStateException("Tipo de usuário " + nome + " não cadastrado."));
    }

    private Usuario buscarAtivo(Long id) {
        return usuarioDAO.buscarAtivoPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    /** Texto sem espaços nas pontas; vazio vira null. */
    private String texto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
