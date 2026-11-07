package br.com.paccellilanches.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Um lanche de um pedido, com o nome e o preço unitário (já com promoção) do momento da compra.
 */
@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    public Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lanche_id")
    public Lanche lanche;

    @Column(nullable = false)
    public String nomeLanche;

    public int quantidade;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Pedido pedido, Lanche lanche, int quantidade, BigDecimal precoUnitario) {
        this.pedido = pedido;
        this.lanche = lanche;
        this.nomeLanche = lanche.nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
