package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("login") // Especifica o template Thymeleaf a ser renderizado (login.html)
    public Map<String, Object> showLogin(@QueryValue Optional<String> erro, @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();
        // O JavaScript no template login.html cuidará da lógica de exibição das mensagens
        return model;
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> login(@Body Map<String, String> form, Session session) {
        String email = form.get("email");
        String senhaFormulario = form.get("senha");

        Optional<Usuario> usuarioOptional = usuarioRepository.buscarPorEmail(email);

        // Verifica se o usuário foi encontrado E se a senha corresponde
        if (usuarioOptional.isPresent() && BCrypt.checkpw(senhaFormulario, usuarioOptional.get().getSenha())) {
            // Se a autenticação for bem-sucedida:
            // Armazena o ID do usuário na sessão
            session.put("usuarioId", usuarioOptional.get().getId());
            // Redireciona de volta para a página de login com o parâmetro de sucesso
            // O JavaScript na página detectará este parâmetro, exibirá a mensagem e fará o redirecionamento final para /lista
            try {
                 URI uri = new URI("/login?sucesso=true"); // <-- Redireciona para a própria página com sucesso=true
                 return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }

        } else {
            // Se a autenticação falhar (usuário não encontrado ou senha incorreta):
            // Redireciona de volta para a página de login, adicionando um parâmetro de erro
            try {
                URI uri = new URI("/login?erro=invalido");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de login.");
            }
        }
    }

    @Get("/logout")
    public HttpResponse<?> logout(Session session) {
        session.remove("usuarioId");
        return HttpResponse.redirect(URI.create("/login"));
    }
}
