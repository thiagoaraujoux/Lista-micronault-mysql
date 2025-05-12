package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;
import org.mindrot.jbcrypt.BCrypt; // Importa a classe BCrypt

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("login") // Especifica o template Thymeleaf a ser renderizado (login.html)
    public Map<String, Object> showLogin() {
        // Retorna um mapa vazio para o template, sem dados específicos por enquanto
        return new HashMap<>();
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> login(@Body Map<String, String> form, Session session) {
        String email = form.get("email");
        String senhaFormulario = form.get("senha"); // Nome da variável alterado para clareza

        // Busca o usuário no banco de dados pelo email
        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        // Verifica se o usuário foi encontrado E se a senha fornecida no formulário
        // corresponde à senha criptografada armazenada no banco de dados
        if (usuario != null && BCrypt.checkpw(senhaFormulario, usuario.getSenha())) {
            // Se a autenticação for bem-sucedida:
            // Armazena o ID do usuário na sessão
            session.put("usuarioId", usuario.getId());
            // Redireciona para a página inicial (/home)
            return HttpResponse.redirect(URI.create("/home"));
        } else {
            // Se a autenticação falhar (usuário não encontrado ou senha incorreta):
            // Redireciona de volta para a página de login, adicionando um parâmetro de erro
            // Você pode usar este parâmetro no template login.html para exibir uma mensagem de erro
            return HttpResponse.redirect(URI.create("/login?erro=true"));
        }
    }

    @Get("/logout")
    public HttpResponse<?> logout(Session session) {
        // Remove o ID do usuário da sessão
        session.remove("usuarioId");
        // Redireciona para a página de login após o logout
        return HttpResponse.redirect(URI.create("/login"));
    }
}
