package br.com.paccellilanches.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario extends PanacheEntity {

    public String nome;
    public String email;
    public String telefone;
    public String cpf;
    public LocalDate dataNascimento;
    public String senha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_usuario_id")
    public TipoUsuario tipo;

    // Exclusão lógica: usuários excluídos ficam com ativo = false
    @Column(columnDefinition = "boolean default true not null")
    public boolean ativo = true;

    public Usuario() {
    }

    public Usuario(String nome, String email, String telefone, String cpf, LocalDate dataNascimento, String senha,
                   TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.tipo = tipo;
    }

    public boolean ehAdmin() {
        return tipo != null && tipo.ehAdmin();
    }
}
