package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.PromocaoDAO;
import br.com.paccellilanches.dto.Moeda;
import br.com.paccellilanches.dto.PromocaoDTO;
import br.com.paccellilanches.entity.Lanche;
import br.com.paccellilanches.entity.Promocao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

    @Inject
    PromocaoDAO promocaoDAO;

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
