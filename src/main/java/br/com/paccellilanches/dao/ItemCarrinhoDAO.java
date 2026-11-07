package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.ItemCarrinho;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ItemCarrinhoDAO implements PanacheRepository<ItemCarrinho> {

    public void salvar(ItemCarrinho item) {
        persist(item);
    }

    public void remover(ItemCarrinho item) {
        delete(item);
    }

    /** Apaga todos os itens do carrinho do usuário (depois de finalizar o pedido). */
    public void esvaziar(Usuario usuario) {
        delete("usuario", usuario);
    }

    /** Itens do carrinho do usuário; lanches retirados do cardápio não aparecem. */
    public List<ItemCarrinho> listarDoUsuario(Usuario usuario) {
        return list("usuario = ?1 and lanche.ativo = true", Sort.ascending("id"), usuario);
    }

    public Optional<ItemCarrinho> buscar(Usuario usuario, Long lancheId) {
        return find("usuario = ?1 and lanche.id = ?2", usuario, lancheId).firstResultOptional();
    }
}
