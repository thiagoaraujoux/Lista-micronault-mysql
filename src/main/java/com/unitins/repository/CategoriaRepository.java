package com.unitins.repository;

import com.unitins.model.Categoria;
import jakarta.inject.Singleton;

import java.sql.*;

@Singleton
public class CategoriaRepository {

    // Configurações de conexão com o banco de dados (idealmente viriam de application.properties)
    private final String URL = "jdbc:mysql://localhost:3306/lista";
    private final String USER = "root";
    private final String PASSWORD = "root";

    // Método para salvar uma nova categoria no banco de dados
    public Categoria salvar(Categoria categoria) {
        // SQL para inserir uma nova categoria. O ID é auto-gerado.
        String sql = "INSERT INTO categoria (nome) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             // Usamos RETURN_GENERATED_KEYS para obter o ID gerado após a inserção
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNome());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar categoria, nenhuma linha afetada.");
            }

            // Obtém o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getLong(1)); // Define o ID no objeto Categoria
                } else {
                    throw new SQLException("Falha ao salvar categoria, nenhum ID gerado obtido.");
                }
            }

            return categoria; // Retorna o objeto Categoria com o ID preenchido

        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar uma RuntimeException ou exceção customizada para erros de banco de dados
            // Você pode adicionar tratamento para nome de categoria duplicado se a coluna for UNIQUE
            throw new RuntimeException("Erro de banco de dados ao salvar categoria", e);
        }
    }

    // Opcional: Método para buscar todas as categorias (útil para dropdowns)
    // public List<Categoria> buscarTodas() { ... }

    // Opcional: Método para buscar categoria por ID
    // public Optional<Categoria> findById(Long id) { ... }
}
