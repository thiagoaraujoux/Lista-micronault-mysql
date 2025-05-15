package com.unitins.model;

// Importa as anotações necessárias
import io.micronaut.core.annotation.Introspected;
import java.util.Objects; // Importa para usar Objects.hash e Objects.equals


// Classe Lista com o novo campo de status
@Introspected
public class Lista {
    private Long id;
    private String titulo;
    private String descricao;
    private Long categoriaId;
    private Long usuarioId;
    private StatusLista status; // Novo campo para o status da lista

    // Construtor padrão (geralmente necessário para frameworks)
    public Lista() {
    }

    // Construtor com campos, incluindo o novo status
    public Lista(String titulo, String descricao, Long categoriaId, Long usuarioId, StatusLista status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
        this.status = status != null ? status : StatusLista.ATIVA; // Define ATIVA como padrão se nulo
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

    // Getter para o novo campo status
    public StatusLista getStatus() {
        return status;
    }

    // Setter para o novo campo status
    public void setStatus(StatusLista status) {
         this.status = status != null ? status : StatusLista.ATIVA; // Garante que o status não seja nulo
    }

    @Override
    public String toString() {
        return "Lista{" +
               "id=" + id +
               ", titulo='" + titulo + '\'' +
               ", descricao='" + descricao + '\'' +
               ", categoriaId=" + categoriaId +
               ", usuarioId=" + usuarioId +
               ", status=" + status + // Inclui o status no toString
               '}';
    }

    // Adicionando equals e hashCode para melhor prática, especialmente se usar coleções
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lista lista = (Lista) o;
        return Objects.equals(id, lista.id); // Comparar pelo ID é comum para igualdade
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash baseado no ID
    }
    
}