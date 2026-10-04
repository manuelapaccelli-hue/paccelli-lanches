package br.com.paccellilanches.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "lanche")
public class Lanche extends PanacheEntity {

    public String nome;
    public String descricao;

    @Column(precision = 10, scale = 2)
    public BigDecimal preco;

    @Enumerated(EnumType.STRING)
    public CategoriaLanche categoria;

    // Exclusão lógica: lanches retirados do cardápio ficam com ativo = false
    @Column(columnDefinition = "boolean default true not null")
    public boolean ativo = true;

    public Lanche() {
    }

    public Lanche(String nome, String descricao, BigDecimal preco, CategoriaLanche categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
    }
}
