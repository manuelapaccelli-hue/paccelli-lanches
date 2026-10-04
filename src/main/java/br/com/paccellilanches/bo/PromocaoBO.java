package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.LancheDAO;
import br.com.paccellilanches.dao.PromocaoDAO;
import br.com.paccellilanches.dto.Moeda;
import br.com.paccellilanches.dto.PromocaoDTO;
import br.com.paccellilanches.dto.PromocaoFormDTO;
import br.com.paccellilanches.dto.PromocaoGestaoDTO;
import br.com.paccellilanches.entity.Lanche;
import br.com.paccellilanches.entity.Promocao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Regras das promoções: quais estão valendo hoje e qual o preço de cada lanche com o desconto.
 */
@ApplicationScoped
public class PromocaoBO {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final int TAMANHO_MAXIMO_SELO = 15;

    @Inject
    PromocaoDAO promocaoDAO;

    @Inject
    LancheDAO lancheDAO;

    @Inject
    AutenticacaoBO autenticacaoBO;

    /** Promoções em vigor hoje, uma por lanche, para a página de promoções. */
    public List<PromocaoDTO> vigentes() {
        return promocoesVigentesPorLanche().values()
                .stream()
                .sorted(Comparator.comparing(promocao -> promocao.id))
                .map(this::paraDTO)
                .toList();
    }

    /**
     * Promoção em vigor hoje de cada lanche (chave: id do lanche). Se um lanche tiver mais de uma,
     * vale a de menor preço.
     */
    public Map<Long, Promocao> promocoesVigentesPorLanche() {
        BinaryOperator<Promocao> maisBarata = (a, b) -> a.precoPromocional.compareTo(b.precoPromocional) <= 0 ? a : b;
        return promocaoDAO.listarVigentes(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(promocao -> promocao.lanche.id, promocao -> promocao, maisBarata));
    }

    /** Preço que o lanche custa hoje: o promocional, se houver promoção vigente, ou o normal. */
    public BigDecimal precoVigente(Lanche lanche, Map<Long, Promocao> promocoes) {
        Promocao promocao = promocoes.get(lanche.id);
        return promocao == null ? lanche.preco : promocao.precoPromocional;
    }

    /** Promoções não encerradas para a tela de gestão, com a situação de cada uma hoje. */
    public List<PromocaoGestaoDTO> listarParaGestao(String sessionId) {
        autenticacaoBO.exigirAdmin(sessionId);
        LocalDate hoje = LocalDate.now();
        return promocaoDAO.listarAtivas()
                .stream()
                .map(promocao -> new PromocaoGestaoDTO(promocao.id, promocao.lanche.id, promocao.lanche.nome,
                        promocao.lanche.preco, promocao.precoPromocional, promocao.selo,
                        promocao.dataInicio, promocao.dataFim, situacao(promocao, hoje)))
                .toList();
    }

    @Transactional
    public void cadastrar(String sessionId, PromocaoFormDTO request) {
        autenticacaoBO.exigirAdmin(sessionId);
        Lanche lanche = validar(request);
        promocaoDAO.salvar(new Promocao(lanche, request.precoPromocional(), seloOuNulo(request.selo()),
                request.dataInicio(), request.dataFim()));
    }

    @Transactional
    public void editar(String sessionId, Long id, PromocaoFormDTO request) {
        autenticacaoBO.exigirAdmin(sessionId);
        Promocao promocao = buscarAtiva(id);
        Lanche lanche = validar(request);

        promocao.lanche = lanche;
        promocao.precoPromocional = request.precoPromocional();
        promocao.selo = seloOuNulo(request.selo());
        promocao.dataInicio = request.dataInicio();
        promocao.dataFim = request.dataFim();
    }

    /** Exclusão lógica: a promoção deixa de valer e sai da lista, mas continua no banco. */
    @Transactional
    public void encerrar(String sessionId, Long id) {
        autenticacaoBO.exigirAdmin(sessionId);
        buscarAtiva(id).ativo = false;
    }

    /** Valida os dados do formulário e devolve o lanche da promoção. */
    private Lanche validar(PromocaoFormDTO request) {
        if (request.lancheId() == null) {
            throw new NegocioException("Escolha o lanche da promoção.");
        }
        Lanche lanche = lancheDAO.buscarAtivoPorId(request.lancheId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lanche não encontrado no cardápio."));

        BigDecimal preco = request.precoPromocional();
        if (preco == null || preco.signum() <= 0) {
            throw new NegocioException("Informe um preço promocional maior que zero.");
        }
        if (preco.scale() > 2) {
            throw new NegocioException("O preço pode ter no máximo duas casas decimais.");
        }
        if (preco.compareTo(lanche.preco) >= 0) {
            throw new NegocioException("O preço promocional deve ser menor que o preço normal ("
                    + Moeda.formatar(lanche.preco) + ").");
        }
        if (request.dataInicio() == null || request.dataFim() == null) {
            throw new NegocioException("Informe as datas de início e de fim.");
        }
        if (request.dataFim().isBefore(request.dataInicio())) {
            throw new NegocioException("A data de fim não pode ser anterior à data de início.");
        }
        if (request.selo() != null && request.selo().trim().length() > TAMANHO_MAXIMO_SELO) {
            throw new NegocioException("O selo pode ter no máximo " + TAMANHO_MAXIMO_SELO + " caracteres.");
        }
        return lanche;
    }

    private String situacao(Promocao promocao, LocalDate hoje) {
        if (promocao.precoPromocional.compareTo(promocao.lanche.preco) >= 0) {
            return "Sem desconto";
        }
        if (hoje.isBefore(promocao.dataInicio)) {
            return "Agendada";
        }
        if (hoje.isAfter(promocao.dataFim)) {
            return "Expirada";
        }
        return "Vigente";
    }

    private Promocao buscarAtiva(Long id) {
        return promocaoDAO.buscarAtivaPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Promoção não encontrada."));
    }

    private static String seloOuNulo(String selo) {
        return selo == null || selo.isBlank() ? null : selo.trim();
    }

    private PromocaoDTO paraDTO(Promocao promocao) {
        Lanche lanche = promocao.lanche;
        String selo = promocao.selo != null ? promocao.selo : percentualDesconto(promocao) + "% OFF";
        return new PromocaoDTO(lanche.id, lanche.nome, lanche.descricao, selo,
                Moeda.formatar(lanche.preco), Moeda.formatar(promocao.precoPromocional),
                Moeda.formatar(lanche.preco.subtract(promocao.precoPromocional)),
                promocao.dataFim.format(DATA));
    }

    private int percentualDesconto(Promocao promocao) {
        BigDecimal desconto = promocao.lanche.preco.subtract(promocao.precoPromocional);
        return desconto.multiply(BigDecimal.valueOf(100))
                .divide(promocao.lanche.preco, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
