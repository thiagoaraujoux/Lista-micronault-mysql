package com.unitins.repository;

import com.unitins.model.Lista;
import jakarta.inject.Singleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ListaRepository {

    // Configurações de conexão com o banco de dados (idealmente viriam de application.properties)
    private final String URL = "jdbc:mysql://localhost:3306/lista";
    private final String USER = "root";
    private final String PASSWORD = "root";

    // Método para salvar uma nova lista no banco de dados
    public Lista salvar(Lista lista) {
        // SQL para inserir uma nova lista. O ID é auto-gerado.
        // Incluímos categoria_id e usuario_id
        String sql = "INSERT INTO lista (titulo, descricao, categoria_id, usuario_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             // Usamos RETURN_GENERATED_KEYS para obter o ID gerado após a inserção
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, lista.getTitulo());
            stmt.setString(2, lista.getDescricao());
            // Define categoria_id (pode ser null se a coluna permitir)
            if (lista.getCategoriaId() != null) {
                 stmt.setLong(3, lista.getCategoriaId());
            } else {
                 stmt.setNull(3, Types.BIGINT); // Define como NULL se categoriaId for null
            }
            // Define usuario_id (não deve ser null se a lista pertence a um usuário logado)
            if (lista.getUsuarioId() != null) {
                stmt.setLong(4, lista.getUsuarioId());
            } else {
                // Lançar um erro ou tratar se usuarioId for null, pois a FK exige um usuário
                throw new IllegalArgumentException("UsuarioId não pode ser nulo ao salvar uma lista.");
            }


            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar lista, nenhuma linha afetada.");
            }

            // Obtém o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lista.setId(generatedKeys.getLong(1)); // Define o ID no objeto Lista
                } else {
                    throw new SQLException("Falha ao salvar lista, nenhum ID gerado obtido.");
                }
            }

            return lista; // Retorna o objeto Lista com o ID preenchido

        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar uma RuntimeException ou exceção customizada para erros de banco de dados
            throw new RuntimeException("Erro de banco de dados ao salvar lista", e);
        }
    }

    // Método para buscar listas por usuário (exemplo, implementar depois)
    public List<Lista> buscarPorUsuario(Long usuarioId) {
        List<Lista> listas = new ArrayList<>();
        // SQL para selecionar listas de um usuário específico
        String sql = "SELECT id, titulo, descricao, categoria_id, usuario_id FROM lista WHERE usuario_id = ?";

         try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Lista lista = new Lista();
                lista.setId(rs.getLong("id"));
                lista.setTitulo(rs.getString("titulo"));
                lista.setDescricao(rs.getString("descricao"));
                lista.setCategoriaId(rs.getLong("categoria_id")); // Pode ser 0 se for NULL no DB, verificar
                lista.setUsuarioId(rs.getLong("usuario_id"));
                listas.add(lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar listas por usuário", e);
        }

        return listas;
    }

    // Você pode adicionar outros métodos (buscar por ID, atualizar, deletar)
}
