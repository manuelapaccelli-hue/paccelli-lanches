package br.com.paccellilanches.bo;

import br.com.paccellilanches.dao.LancheDAO;
import br.com.paccellilanches.dto.CategoriaCardapioDTO;
import br.com.paccellilanches.dto.LancheDTO;
import br.com.paccellilanches.entity.CategoriaLanche;
import br.com.paccellilanches.entity.Lanche;
import br.com.paccellilanches.entity.Promocao;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Regras de negócio dos lanches e montagem do cardápio.
 */
@ApplicationScoped
public class LancheBO {

    @Inject
    LancheDAO lancheDAO;

    @Inject
    PromocaoBO promocaoBO;

    /**
     * Lanches ativos agrupados por categoria, na ordem do cardápio; categorias vazias são omitidas.
     * Lanches com promoção vigente trazem o preço promocional.
     */
    public List<CategoriaCardapioDTO> cardapio() {
        Map<Long, Promocao> promocoes = promocaoBO.promocoesVigentesPorLanche();
        Map<CategoriaLanche, List<LancheDTO>> porCategoria = lancheDAO.listarAtivos()
                .stream()
                .collect(Collectors.groupingBy(lanche -> lanche.categoria,
                        Collectors.mapping(lanche -> paraDTO(lanche, promocoes.get(lanche.id)), Collectors.toList())));

        return Arrays.stream(CategoriaLanche.values())
                .filter(porCategoria::containsKey)
                .map(categoria -> new CategoriaCardapioDTO(categoria.titulo, porCategoria.get(categoria)))
                .toList();
    }

    /** Na primeira execução, com a tabela vazia, cadastra o cardápio inicial da lanchonete. */
    @Transactional
    void popularCardapioInicial(@Observes StartupEvent evento) {
        if (lancheDAO.existeAlgum()) {
            return;
        }

        List.of(
                new Lanche("Paccelli Clássico", "Hambúrguer 150g, queijo cheddar, alface, tomate, cebola e molho especial", new BigDecimal("18.90"), CategoriaLanche.HAMBURGUER),
                new Lanche("Paccelli Bacon", "Hambúrguer 150g, bacon crocante, queijo cheddar, alface, tomate e molho barbecue", new BigDecimal("22.90"), CategoriaLanche.HAMBURGUER),
                new Lanche("Paccelli Duplo", "Dois hambúrgueres 150g, queijo cheddar, cebola caramelizada e molho especial", new BigDecimal("28.90"), CategoriaLanche.HAMBURGUER),
                new Lanche("Paccelli Supremo", "Hambúrguer 200g, queijo prato, cheddar, bacon, ovo, alface, tomate e molho da casa", new BigDecimal("32.90"), CategoriaLanche.HAMBURGUER),
                new Lanche("X-Tudo", "Hambúrguer, queijo, presunto, bacon, ovo, alface, tomate, milho e batata palha", new BigDecimal("24.90"), CategoriaLanche.LANCHE_QUENTE),
                new Lanche("X-Bacon", "Hambúrguer, bacon, queijo cheddar, alface e tomate", new BigDecimal("21.90"), CategoriaLanche.LANCHE_QUENTE),
                new Lanche("X-Frango", "Filé de frango grelhado, queijo, alface, tomate e maionese temperada", new BigDecimal("19.90"), CategoriaLanche.LANCHE_QUENTE),
                new Lanche("Batata Frita", "Porção generosa com cheddar e bacon (opcional)", new BigDecimal("16.90"), CategoriaLanche.PORCAO),
                new Lanche("Anéis de Cebola", "Servidos com molho rosé", new BigDecimal("14.90"), CategoriaLanche.PORCAO),
                new Lanche("Coxinha da Casa (6 unid)", "Recheio de frango com catupiry", new BigDecimal("18.90"), CategoriaLanche.PORCAO),
                new Lanche("Refrigerante 350ml", "Coca-Cola, Guaraná, Sprite, Fanta", new BigDecimal("6.00"), CategoriaLanche.BEBIDA),
                new Lanche("Suco Natural 500ml", "Laranja, Maracujá, Limão ou Misto", new BigDecimal("9.90"), CategoriaLanche.BEBIDA),
                new Lanche("Milk Shake 500ml", "Chocolate, Morango, Ovomaltine ou Baunilha", new BigDecimal("14.90"), CategoriaLanche.BEBIDA)
        ).forEach(lancheDAO::salvar);
    }

    private LancheDTO paraDTO(Lanche lanche, Promocao promocao) {
        return new LancheDTO(lanche.id, lanche.nome, lanche.descricao, lanche.preco,
                promocao == null ? null : promocao.precoPromocional);
    }
}
