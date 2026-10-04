package br.com.paccellilanches.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Desconto em um lanche do cardápio durante um período: entre dataInicio e dataFim
 * (inclusive), o lanche passa a custar precoPromocional.
 */
@Entity
@Table(name = "promocao")
public class Promocao extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "lanche_id")
    public Lanche lanche;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal precoPromocional;

    /** Texto do selo exibido no card (ex.: "TOP"); sem selo, é mostrado o percentual de desconto. */
    public String selo;

    @Column(nullable = false)
    public LocalDate dataInicio;

    @Column(nullable = false)
    public LocalDate dataFim;

    // Exclusão lógica: promoções encerradas manualmente ficam com ativo = false
    @Column(columnDefinition = "boolean default true not null")
    public boolean ativo = true;

    public Promocao() {
    }

    public Promocao(Lanche lanche, BigDecimal precoPromocional, String selo, LocalDate dataInicio, LocalDate dataFim) {
        this.lanche = lanche;
        this.precoPromocional = precoPromocional;
        this.selo = selo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
}
