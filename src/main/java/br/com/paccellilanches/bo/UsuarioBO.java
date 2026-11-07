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
import java.util.regex.Pattern;

/**
 * Regras de negócio do usuário: autocadastro, perfil (dados e senha) e gerenciamento pelos administradores.
 */
@ApplicationScoped
public class UsuarioBO {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private static final int TAMANHO_MINIMO_NOME = 3;

    private static final int TAMANHO_MAXIMO_TEXTO = 100;

    private static final int IDADE_MAXIMA = 120;

    /** nome@dominio.extensao, sem espaços. */
    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

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
        if (texto(request.telefone()) == null) {
            throw new NegocioException("Informe um telefone para contato.");
        }
        DadosUsuario dados = validarDados(request.nome(), request.email(), request.telefone(), request.cpf(),
                request.dataNascimento(), null);
        validarSenha(request.senha());
        usuarioDAO.salvar(novoUsuario(dados, request.senha(), buscarTipo(TipoUsuario.COMUM)));
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
        aplicarDados(usuario, validarDados(request.nome(), request.email(), request.telefone(), request.cpf(),
                request.dataNascimento(), usuario.id));
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
        DadosUsuario dados = validarDados(request.nome(), request.email(), request.telefone(), request.cpf(),
                request.dataNascimento(), null);
        validarSenha(request.senha());
        TipoUsuario tipo = buscarTipoInformado(request.tipo());
        usuarioDAO.salvar(novoUsuario(dados, request.senha(), tipo));
    }

    /**
     * O administrador edita os dados de um usuário. A senha só muda se for informada, e o
     * administrador não pode tirar a própria permissão de administrador.
     */
    @Transactional
    public void editarPeloAdmin(String sessionId, Long id, UsuarioFormDTO request) {
        Usuario logado = autenticacaoBO.exigirAdmin(sessionId);
        Usuario usuario = buscarAtivo(id);
        DadosUsuario dados = validarDados(request.nome(), request.email(), request.telefone(), request.cpf(),
                request.dataNascimento(), usuario.id);
        TipoUsuario tipo = buscarTipoInformado(request.tipo());
        if (usuario.id.equals(logado.id) && !tipo.ehAdmin()) {
            throw new NegocioException("Você não pode remover a sua própria permissão de administrador.");
        }
        if (texto(request.senha()) != null) {
            validarSenha(request.senha());
            usuario.senha = request.senha();
        }
        aplicarDados(usuario, dados);
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

    /** Dados pessoais já validados e no formato em que são gravados. */
    private record DadosUsuario(String nome, String email, String telefone, String cpf, LocalDate dataNascimento) {
    }

    /**
     * Valida os dados pessoais e devolve-os normalizados (CPF e telefone formatados).
     * Nome e e-mail são obrigatórios; telefone, CPF e data de nascimento, quando informados, precisam ser
     * válidos. E-mail e CPF não podem ser de outro usuário (idAtual = null no cadastro).
     */
    private DadosUsuario validarDados(String nome, String email, String telefone, String cpf,
                                      LocalDate dataNascimento, Long idAtual) {
        return new DadosUsuario(validarNome(nome), validarEmail(email, idAtual), validarTelefone(telefone),
                validarCpf(cpf, idAtual), validarDataNascimento(dataNascimento));
    }

    private String validarNome(String valor) {
        String nome = texto(valor);
        if (nome == null) {
            throw new NegocioException("Informe o nome.");
        }
        if (nome.length() < TAMANHO_MINIMO_NOME || nome.length() > TAMANHO_MAXIMO_TEXTO) {
            throw new NegocioException("O nome deve ter entre " + TAMANHO_MINIMO_NOME + " e "
                    + TAMANHO_MAXIMO_TEXTO + " caracteres.");
        }
        return nome;
    }

    private String validarEmail(String valor, Long idAtual) {
        String email = texto(valor);
        if (email == null) {
            throw new NegocioException("Informe o e-mail.");
        }
        if (email.length() > TAMANHO_MAXIMO_TEXTO || !FORMATO_EMAIL.matcher(email).matches()) {
            throw new NegocioException("Informe um e-mail válido (ex.: nome@email.com).");
        }
        boolean emailEmUso = idAtual == null
                ? usuarioDAO.existePorEmail(email)
                : usuarioDAO.existeOutroComEmail(email, idAtual);
        if (emailEmUso) {
            throw new NegocioException("Já existe uma conta cadastrada com este e-mail.");
        }
        return email;
    }

    /** Telefone com DDD: 10 dígitos (fixo) ou 11 (celular, começando com 9). Gravado como (61) 99999-9999. */
    private String validarTelefone(String valor) {
        if (texto(valor) == null) {
            return null;
        }
        String digitos = valor.replaceAll("\\D", "");
        boolean fixo = digitos.length() == 10;
        boolean celular = digitos.length() == 11 && digitos.charAt(2) == '9';
        if ((!fixo && !celular) || digitos.charAt(0) == '0') {
            throw new NegocioException("Informe um telefone válido com DDD, ex.: (61) 99999-9999.");
        }
        int meio = digitos.length() - 4;
        return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, meio) + "-" + digitos.substring(meio);
    }

    /** CPF com dígitos verificadores válidos e sem repetir entre usuários. Gravado como 000.000.000-00. */
    private String validarCpf(String valor, Long idAtual) {
        if (texto(valor) == null) {
            return null;
        }
        String digitos = valor.replaceAll("\\D", "");
        if (!cpfValido(digitos)) {
            throw new NegocioException("CPF inválido. Confira os números digitados.");
        }
        String cpf = digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "." + digitos.substring(6, 9)
                + "-" + digitos.substring(9);
        if (usuarioDAO.existeOutroComCpf(cpf, idAtual)) {
            throw new NegocioException("Já existe uma conta cadastrada com este CPF.");
        }
        return cpf;
    }

    /**
     * Confere os dois dígitos verificadores do CPF: cada um é o resto da soma ponderada dos dígitos
     * anteriores (pesos decrescentes) multiplicada por 10, dividido por 11 (resto 10 vira 0).
     * CPFs com todos os dígitos iguais (ex.: 111.111.111-11) passam na conta, mas não existem.
     */
    private boolean cpfValido(String digitos) {
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }
        for (int posicao = 9; posicao <= 10; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) {
                soma += (digitos.charAt(i) - '0') * (posicao + 1 - i);
            }
            int digito = (soma * 10) % 11 % 10;
            if (digito != digitos.charAt(posicao) - '0') {
                return false;
            }
        }
        return true;
    }

    private LocalDate validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return null;
        }
        LocalDate hoje = LocalDate.now();
        if (dataNascimento.isAfter(hoje)) {
            throw new NegocioException("A data de nascimento não pode ser uma data futura.");
        }
        if (dataNascimento.isBefore(hoje.minusYears(IDADE_MAXIMA))) {
            throw new NegocioException("Informe uma data de nascimento válida.");
        }
        return dataNascimento;
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new NegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

    private Usuario novoUsuario(DadosUsuario dados, String senha, TipoUsuario tipo) {
        return new Usuario(dados.nome(), dados.email(), dados.telefone(), dados.cpf(), dados.dataNascimento(),
                senha, tipo);
    }

    /** Copia os dados validados para o usuário; se o e-mail mudou, as sessões abertas acompanham. */
    private void aplicarDados(Usuario usuario, DadosUsuario dados) {
        if (!dados.email().equals(usuario.email)) {
            sessaoBO.atualizarEmail(usuario.email, dados.email());
        }
        usuario.nome = dados.nome();
        usuario.email = dados.email();
        usuario.telefone = dados.telefone();
        usuario.cpf = dados.cpf();
        usuario.dataNascimento = dados.dataNascimento();
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
