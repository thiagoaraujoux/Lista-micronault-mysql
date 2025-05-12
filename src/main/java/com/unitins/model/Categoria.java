package com.unitins.model;

// Importa a anotação Introspected para que o Micronaut possa gerar metadados para este bean
import io.micronaut.core.annotation.Introspected;

// Anotação Introspected é necessária para que o Micronaut possa trabalhar com este bean
@Introspected
public class Categoria {
    private Long id;
    private String nome;

    // Construtor padrão
    public Categoria() {
    }

    // Construtor com campos
    public Categoria(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Categoria{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               '}';
    }
}
