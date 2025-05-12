package com.unitins.repository;

import com.unitins.model.Usuario;
import com.unitins.repository.exceptions.DuplicateEmailException; // Importa a nova exceção
import org.mindrot.jbcrypt.BCrypt;
import jakarta.inject.Singleton;

import java.sql.*;
import java.util.Optional;

@Singleton
public class UsuarioRepository {

    private final String URL = "jdbc:mysql://localhost:3306/lista";
    private final String USER = "root";
    private final String PASSWORD = "root";

    // Método para buscar um usuário por email
    public Optional<Usuario> buscarPorEmail(String email) { // Retorna Optional<Usuario>
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE email = ?"; // Inclui nome na busca por email

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                return Optional.of(usuario); // Retorna Optional contendo o usuário
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Para erros SQL genéricos na busca, ainda lançamos uma RuntimeException ou logamos
            throw new RuntimeException("Erro de banco de dados ao buscar usuário por email", e);
        }

        return Optional.empty(); // Retorna Optional vazio se não encontrar
    }

    // Método para buscar um usuário por ID
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                return Optional.of(usuario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar usuário por ID", e);
        }

        return Optional.empty();
    }

    // Método para salvar um novo usuário com tratamento de erro para email duplicado
    public void salvar(Usuario usuario) {
        String hashedPassword = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Verifica se é um erro de violação de chave única (código 1062 para MySQL)
            if (e.getErrorCode() == 1062) {
                // Lança a exceção customizada para email duplicado
                throw new DuplicateEmailException(usuario.getEmail(), e);
            } else {
                // Para outros erros SQL, lança uma RuntimeException genérica
                e.printStackTrace();
                throw new RuntimeException("Erro de banco de dados ao salvar usuário", e);
            }
        }
    }
}
