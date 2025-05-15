package com.unitins.model;

import io.micronaut.core.annotation.Introspected;
import java.util.Objects;

// Anotação Introspected para que o Micronaut possa trabalhar com este bean
@Introspected
public class Categoria {
    private Long id;
    private String nome;

    // Construtor padrão
    public Categoria() {
    }

    // Construtor com campos
    public Categoria(Long id, String nome) {
        this.id = id;
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

    // equals e hashCode baseados no ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
