package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.ItemCarrinho;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ItemCarrinhoDAO {

    public void salvar(ItemCarrinho item) {
        item.persist();
    }

    public void remover(ItemCarrinho item) {
        item.delete();
    }

    /** Itens do carrinho do usuário; lanches retirados do cardápio não aparecem. */
    public List<ItemCarrinho> listarDoUsuario(Usuario usuario) {
        return ItemCarrinho.list("usuario = ?1 and lanche.ativo = true", Sort.ascending("id"), usuario);
    }

    public Optional<ItemCarrinho> buscar(Usuario usuario, Long lancheId) {
        return ItemCarrinho.find("usuario = ?1 and lanche.id = ?2", usuario, lancheId).firstResultOptional();
    }
}
