package com.unitins.model;

// Importa a anotação Introspected para que o Micronaut possa gerar metadados para este bean
import io.micronaut.core.annotation.Introspected;

// Anotação Introspected é necessária para que o Micronaut possa trabalhar com este bean
// (por exemplo, para serialização/desserialização ou para passar como modelo para views)
@Introspected
public class Lista {
    private Long id;
    private String titulo;
    private String descricao;
    private Long categoriaId;
    private Long usuarioId;

    // Construtor padrão (geralmente necessário para frameworks)
    public Lista() {
    }

    // Construtor com campos (opcional, mas útil)
    public Lista(String titulo, String descricao, Long categoriaId, Long usuarioId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "Lista{" +
               "id=" + id +
               ", titulo='" + titulo + '\'' +
               ", descricao='" + descricao + '\'' +
               ", categoriaId=" + categoriaId +
               ", usuarioId=" + usuarioId +
               '}';
    }
}
