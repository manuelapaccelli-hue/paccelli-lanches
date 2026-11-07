package br.com.paccellilanches.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Um lanche no carrinho de um usuário; cada lanche aparece uma vez por carrinho, com a sua quantidade.
 */
@Entity
@Table(name = "item_carrinho", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "lanche_id"}))
public class ItemCarrinho {

    @Id
    @GeneratedValue
    public Long id;

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
}
