package br.com.paccellilanches.bo;

import br.com.paccellilanches.audit.ContextoAuditoria;
import br.com.paccellilanches.dao.AuditLogDAO;
import br.com.paccellilanches.dto.AuditLogDTO;
import br.com.paccellilanches.entity.AuditLog;
import io.quarkus.arc.Arc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rastreabilidade: grava e lista o log de auditoria e traduz as requisições para ações legíveis.
 */
@ApplicationScoped
public class AuditoriaBO {

    /** Quantas ações a tela de auditoria mostra (as mais recentes). */
    public static final int LIMITE_TELA = 300;

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Descrição de cada rota da aplicação; {id} é trocado pelo número que aparece no caminho. */
    private static final List<Rota> ROTAS = List.of(
            new Rota("GET", "", "Acessou a página inicial"),
            new Rota("GET", "cardapio", "Acessou o cardápio"),
            new Rota("GET", "promocoes", "Acessou as promoções"),
            new Rota("GET", "contato", "Acessou a página de contato"),
            new Rota("GET", "login", "Acessou a tela de login"),
            new Rota("POST", "login", "Tentou fazer login"),
            new Rota("GET", "login/status", "Consultou a sessão"),
            new Rota("DELETE", "login/sessao", "Saiu do sistema"),
            new Rota("GET", "cadastro", "Acessou a tela de cadastro"),
            new Rota("POST", "cadastro", "Criação de conta"),
            new Rota("GET", "perfil", "Acessou o perfil"),
            new Rota("PUT", "perfil/dados", "Alteração dos próprios dados"),
            new Rota("PUT", "perfil/senha", "Troca da própria senha"),
            new Rota("GET", "carrinho", "Acessou o carrinho"),
            new Rota("GET", "carrinho/itens", "Consultou os itens do carrinho"),
            new Rota("POST", "carrinho/itens", "Adição de item ao carrinho"),
            new Rota("PUT", "carrinho/itens/(\\d+)", "Alteração de quantidade no carrinho (lanche #{id})"),
            new Rota("DELETE", "carrinho/itens/(\\d+)", "Remoção de item do carrinho (lanche #{id})"),
            new Rota("GET", "pedidos", "Acessou Meus pedidos"),
            new Rota("POST", "pedidos", "Finalização de pedido"),
            new Rota("GET", "usuarios", "Acessou a gestão de usuários"),
            new Rota("POST", "usuarios", "Cadastro de usuário"),
            new Rota("PUT", "usuarios/(\\d+)", "Edição do usuário #{id}"),
            new Rota("DELETE", "usuarios/(\\d+)", "Exclusão do usuário #{id}"),
            new Rota("GET", "auditoria", "Acessou o log de auditoria"),
            new Rota("GET", "gestao-cardapio", "Acessou a gestão do cardápio"),
            new Rota("POST", "gestao-cardapio/lanches", "Cadastro de lanche"),
            new Rota("PUT", "gestao-cardapio/lanches/(\\d+)", "Edição do lanche #{id}"),
            new Rota("DELETE", "gestao-cardapio/lanches/(\\d+)", "Retirada do lanche #{id} do cardápio"),
            new Rota("GET", "gestao-promocoes", "Acessou a gestão de promoções"),
            new Rota("POST", "gestao-promocoes/promocoes", "Cadastro de promoção"),
            new Rota("PUT", "gestao-promocoes/promocoes/(\\d+)", "Edição da promoção #{id}"),
            new Rota("DELETE", "gestao-promocoes/promocoes/(\\d+)", "Encerramento da promoção #{id}"),
            new Rota("GET", "gestao-pedidos", "Acessou a gestão de pedidos"),
            new Rota("PUT", "gestao-pedidos/pedidos/(\\d+)/status", "Alteração de status do pedido #{id}")
    );

    @Inject
    AuditLogDAO auditLogDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    @Transactional
    public void registrar(String usuario, String acao, String requisicao, int status, String detalhe) {
        auditLogDAO.salvar(new AuditLog(usuario, acao, requisicao, status, detalhe, LocalDateTime.now()));
    }

    public List<AuditLogDTO> listarRecentes(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        return auditLogDAO.listarRecentes(LIMITE_TELA)
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    /** Ação legível de uma requisição, pela tabela de rotas; rota desconhecida fica como "MÉTODO /caminho". */
    public String descrever(String metodo, String caminho) {
        String rota = caminho.replaceAll("^/+|/+$", "");
        for (Rota candidata : ROTAS) {
            Matcher matcher = candidata.padrao.matcher(rota);
            if (candidata.metodo.equals(metodo) && matcher.matches()) {
                return matcher.groupCount() > 0
                        ? candidata.descricao.replace("{id}", matcher.group(1))
                        : candidata.descricao;
            }
        }
        return metodo + " /" + rota;
    }

    // ----- Usados pelos outros BOs para detalhar a ação da requisição atual -----

    /** Troca a descrição padrão da rota por uma mais específica (ex.: "Adicionou X-Tudo ao carrinho"). */
    public void detalhar(String descricao) {
        contexto().ifPresent(contexto -> contexto.descrever(descricao));
    }

    /** Informa o usuário da ação quando ele ainda não tem sessão (ex.: e-mail digitado no login). */
    public void identificarUsuario(String usuario) {
        contexto().ifPresent(contexto -> contexto.identificarUsuario(usuario));
    }

    /** Guarda a mensagem de erro da ação recusada. */
    public void registrarErro(String mensagem) {
        contexto().ifPresent(contexto -> contexto.registrarErro(mensagem));
    }

    /** Só existe dentro de uma requisição HTTP; na inicialização (cardápio inicial) não há o que registrar. */
    private Optional<ContextoAuditoria> contexto() {
        if (!Arc.container().requestContext().isActive()) {
            return Optional.empty();
        }
        return Optional.of(Arc.container().instance(ContextoAuditoria.class).get());
    }

    private AuditLogDTO paraDTO(AuditLog log) {
        Integer status = log.status;
        return new AuditLogDTO(log.dataHora.format(DATA_HORA), log.usuario, log.acao, log.requisicao,
                resultado(status), resultadoCss(status), log.detalhe);
    }

    private String resultado(Integer status) {
        if (status == null) {
            return "—";
        }
        if (status < 300) {
            return "Sucesso";
        }
        if (status < 400) {
            return "Redirecionado";
        }
        return switch (status) {
            case 400 -> "Recusado";
            case 401 -> "Sem login";
            case 403 -> "Acesso negado";
            case 404 -> "Não encontrado";
            default -> status >= 500 ? "Erro no servidor" : "Erro " + status;
        };
    }

    private String resultadoCss(Integer status) {
        if (status == null) {
            return "neutro";
        }
        if (status < 300) {
            return "sucesso";
        }
        return status < 400 ? "aviso" : "erro";
    }

    private record Rota(String metodo, Pattern padrao, String descricao) {
        Rota(String metodo, String caminho, String descricao) {
            this(metodo, Pattern.compile(caminho), descricao);
        }
    }
}
