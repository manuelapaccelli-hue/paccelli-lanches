package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.ItemPedido;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemPedidoDAO implements PanacheRepository<ItemPedido> {

    public void salvar(ItemPedido item) {
        persist(item);
    }
}
