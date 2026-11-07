package br.com.paccellilanches.entity;

/**
 * Etapas de um pedido, na ordem em que acontecem. ENTREGUE e CANCELADO encerram o pedido.
 */
public enum StatusPedido {
    RECEBIDO("Recebido"),
    EM_PREPARO("Em preparo"),
    SAIU_PARA_ENTREGA("Saiu para entrega"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    public final String titulo;

    StatusPedido(String titulo) {
        this.titulo = titulo;
    }

    public boolean encerrado() {
        return this == ENTREGUE || this == CANCELADO;
    }
}
