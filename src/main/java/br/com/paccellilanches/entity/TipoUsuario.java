package br.com.paccellilanches.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_usuario")
public class TipoUsuario {

    @Id
    @GeneratedValue
    public Long id;

    public static final String ADMIN = "ADMIN";
    public static final String COMUM = "COMUM";

    @Column(unique = true, nullable = false)
    public String nome;

    public String descricao;

    public TipoUsuario() {
    }

    public TipoUsuario(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public boolean ehAdmin() {
        return ADMIN.equals(nome);
    }
}
