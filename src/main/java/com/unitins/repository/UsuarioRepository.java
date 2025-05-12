package com.unitins.repository;

import com.unitins.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.inject.Singleton;

import java.sql.*;
import java.util.Optional; // Importa Optional

@Singleton
public class UsuarioRepository {

    private final String URL = "jdbc:mysql://localhost:3306/lista";
    private final String USER = "root";
    private final String PASSWORD = "root";

    // Método para buscar um usuário por email (já existente)
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT id, email, senha FROM usuario WHERE email = ?"; // Seleciona apenas colunas necessárias

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha")); // Senha criptografada
                return usuario;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar uma RuntimeException ou retornar Optional.empty() pode ser melhor dependendo da estratégia de tratamento de erros
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        }

        return null; // Retorna null se não encontrar
    }

    // Método para buscar um usuário por ID (NOVO)
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE id = ?"; // Seleciona colunas, incluindo nome

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome")); // Obtém o nome
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                return Optional.of(usuario); // Retorna Optional contendo o usuário
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar uma RuntimeException ou retornar Optional.empty()
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        }

        return Optional.empty(); // Retorna Optional vazio se não encontrar ou ocorrer erro
    }


    // Método para salvar um novo usuário (já existente)
    public void salvar(Usuario usuario) {
        // Criptografa a senha antes de salvar
        String hashedPassword = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        // Inclui 'nome' na query de insert
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, hashedPassword); // Salva a senha criptografada
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

}
