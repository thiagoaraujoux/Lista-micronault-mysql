package com.unitins.model;

import io.micronaut.core.annotation.Introspected;

// Define um enum para os possíveis status da lista
@Introspected // Anotação para Micronaut
public enum StatusLista {
    // Define os status possíveis para a lista
    ATIVA("Ativa"),         // A lista está em uso
    CONCLUIDA("Concluída"), // Todos os itens da lista foram completados
    ARQUIVADA("Arquivada"); // A lista foi arquivada (não mais ativa, mas mantida para referência)

    private final String descricao; // Descrição amigável do status

    // Construtor do enum
    StatusLista(String descricao) {
        this.descricao = descricao;
    }

    // Getter para a descrição
    public String getDescricao() {
        return descricao;
    }

    // Método estático para encontrar o enum pela descrição (útil para conversão)
    public static StatusLista fromDescricao(String descricao) {
        for (StatusLista status : values()) {
            if (status.descricao.equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        // Opcional: lançar exceção ou retornar um valor padrão se a descrição não for encontrada
        throw new IllegalArgumentException("Status de lista desconhecido: " + descricao);
    }
}