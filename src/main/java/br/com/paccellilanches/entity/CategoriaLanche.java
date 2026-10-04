package br.com.paccellilanches.entity;

/**
 * Seções do cardápio, na ordem em que são exibidas.
 */
public enum CategoriaLanche {

    HAMBURGUER("🍔 HAMBÚRGUERES"),
    LANCHE_QUENTE("🔥 LANCHES QUENTES"),
    PORCAO("🍟 PORÇÕES"),
    BEBIDA("🥤 BEBIDAS");

    public final String titulo;

    CategoriaLanche(String titulo) {
        this.titulo = titulo;
    }
}
