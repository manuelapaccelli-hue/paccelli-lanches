package br.com.paccellilanches.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Compra finalizada a partir do carrinho. Guarda o total e os itens com os preços do momento
 * da compra, para que mudanças futuras no cardápio ou nas promoções não alterem o pedido.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    public Usuario usuario;

    @Column(nullable = false)
    public LocalDateTime dataHora;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public StatusPedido status = StatusPedido.RECEBIDO;

    @OneToMany(mappedBy = "pedido")
    @OrderBy("id")
    public List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Usuario usuario, LocalDateTime dataHora) {
        this.usuario = usuario;
        this.dataHora = dataHora;
    }
}
