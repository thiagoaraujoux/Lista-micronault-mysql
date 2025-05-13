package com.unitins.repository;

import com.unitins.model.Lista;
import jakarta.inject.Singleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Importar Optional

@Singleton
public class ListaRepository {

    // Configurações de conexão com o banco de dados (idealmente viriam de application.properties)
    private final String URL = "jdbc:mysql://localhost:3306/lista";
    private final String USER = "root";
    private final String PASSWORD = "root";

    // Método para salvar uma nova lista no banco de dados (seu código existente)
    // Este método faz um INSERT. Se precisar de um salvar que faz INSERT ou UPDATE
    // dependendo se o ID está preenchido, precisaria ajustar a lógica.
    public Lista salvar(Lista lista) {
        // SQL para inserir uma nova lista. O ID é auto-gerado.
        String sql = "INSERT INTO lista (titulo, descricao, categoria_id, usuario_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             // Usamos RETURN_GENERATED_KEYS para obter o ID gerado após a inserção
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, lista.getTitulo());
            stmt.setString(2, lista.getDescricao());

            if (lista.getCategoriaId() != null) {
                stmt.setLong(3, lista.getCategoriaId());
            } else {
                stmt.setNull(3, Types.BIGINT); // Define como NULL se categoriaId for null
            }

            if (lista.getUsuarioId() != null) {
                stmt.setLong(4, lista.getUsuarioId());
            } else {
                 throw new IllegalArgumentException("UsuarioId não pode ser nulo ao salvar uma lista.");
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar lista, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lista.setId(generatedKeys.getLong(1)); // Define o ID no objeto Lista
                } else {
                    throw new SQLException("Falha ao salvar lista, nenhum ID gerado obtido.");
                }
            }

            return lista;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao salvar lista", e);
        }
    }

    // Método para buscar listas por usuário (seu código existente)
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
                 // Lidar com NULL na coluna categoria_id do banco
                Long categoriaId = rs.getLong("categoria_id");
                if (rs.wasNull()) { // Verifica se o valor lido do DB era NULL
                    lista.setCategoriaId(null);
                } else {
                    lista.setCategoriaId(categoriaId);
                }
                lista.setUsuarioId(rs.getLong("usuario_id"));
                listas.add(lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar listas por usuário", e);
        }

        return listas;
    }

    // >>> MÉTODO CORRIGIDO: Buscar lista por ID <<<
    public Optional<Lista> findById(Long id) {
        String sql = "SELECT id, titulo, descricao, categoria_id, usuario_id FROM lista WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Lista lista = new Lista();
                lista.setId(rs.getLong("id"));
                lista.setTitulo(rs.getString("titulo"));
                lista.setDescricao(rs.getString("descricao"));
                 // Lidar com NULL na coluna categoria_id do banco
                 Long categoriaId = rs.getLong("categoria_id");
                 if (rs.wasNull()) {
                     lista.setCategoriaId(null);
                 } else {
                     lista.setCategoriaId(categoriaId);
                 }
                lista.setUsuarioId(rs.getLong("usuario_id"));
                return Optional.of(lista); // Retorna um Optional contendo a lista encontrada
            } else {
                return Optional.empty(); // Retorna um Optional vazio se não encontrar a lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar lista por ID", e);
        }
    }


    // >>> MÉTODO CORRIGIDO: Atualizar lista existente <<<
    public void atualizar(Lista lista) {
        // SQL para atualizar uma lista existente pelo ID
        String sql = "UPDATE lista SET titulo = ?, descricao = ?, categoria_id = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lista.getTitulo());
            stmt.setString(2, lista.getDescricao());

            if (lista.getCategoriaId() != null) {
                stmt.setLong(3, lista.getCategoriaId());
            } else {
                stmt.setNull(3, Types.BIGINT); // Define como NULL no DB
            }
            stmt.setLong(4, lista.getId()); // Condição WHERE pelo ID

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                 // Pode lançar um erro se a lista não for encontrada para atualizar (ID inválido)
                 throw new SQLException("Falha ao atualizar lista, nenhuma linha afetada (ID: " + lista.getId() + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao atualizar lista", e);
        }
    }

    // >>> MÉTODO CORRIGIDO: Excluir lista por ID <<<
    public void excluir(Long id) {
        // SQL para excluir uma lista pelo ID
        String sql = "DELETE FROM lista WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id); // Condição WHERE pelo ID

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                // Pode lançar um erro se a lista não for encontrada para excluir (ID inválido)
                 throw new SQLException("Falha ao excluir lista, nenhuma linha afetada (ID: " + id + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao excluir lista", e);
        }
    }

    // Você pode adicionar outros métodos de repositório se precisar (ex: buscar categorias por ID ou todas)
    // public Optional<Categoria> findCategoriaById(Long id) { ... }
    // public List<Categoria> findAllCategorias() { ... }
}