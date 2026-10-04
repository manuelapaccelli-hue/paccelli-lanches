package br.com.paccellilanches.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/**
 * Um lanche no carrinho de um usuário; cada lanche aparece uma vez por carrinho, com a sua quantidade.
 */
@Entity
@Table(name = "item_carrinho", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "lanche_id"}))
public class ItemCarrinho extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    public Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lanche_id")
    public Lanche lanche;

    public int quantidade;

    public ItemCarrinho() {
    }

    public ItemCarrinho(Usuario usuario, Lanche lanche, int quantidade) {
        this.usuario = usuario;
        this.lanche = lanche;
        this.quantidade = quantidade;
    }

    public BigDecimal subtotal() {
        return lanche.preco.multiply(BigDecimal.valueOf(quantidade));
    }
}
