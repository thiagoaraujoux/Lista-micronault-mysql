package com.unitins.controller;

import com.unitins.model.Usuario; // Importa a classe Usuario
import com.unitins.repository.UsuarioRepository; // Importa o repositório de usuário
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.views.View;
import java.net.URI;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Controller("/")
public class HomeController {

    private final UsuarioRepository usuarioRepository; // Injeta o repositório

    // Construtor para injetar o UsuarioRepository
    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Get
    @View("home") // Especifica o template Thymeleaf a ser renderizado (home.html)
    public Map<String, Object> home(Session session) {
        // Prepara um mapa para passar dados para o template
        Map<String, Object> model = new HashMap<>();

        // Verifica se o atributo "usuarioId" existe na sessão
        Optional<Object> usuarioIdOptional = session.get("usuarioId");

        boolean isAuthenticated = usuarioIdOptional.isPresent();
        model.put("isAuthenticated", isAuthenticated); // Passa o estado de autenticação para o template

        if (isAuthenticated) {
            // Se o usuário estiver autenticado, tenta buscar o usuário pelo ID na sessão
            try {
                Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
                Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId); // Assume que o repositório tem findById

                if (usuarioOptional.isPresent()) {
                    // Se o usuário for encontrado, adiciona o nome dele ao modelo
                    model.put("userName", usuarioOptional.get().getNome());
                } else {
                    // Caso o usuário não seja encontrado (ID na sessão inválido?),
                    // remove a sessão e redireciona para login.
                    session.remove("usuarioId");
                    throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
                }
            } catch (NumberFormatException e) {
                // Se o usuarioId na sessão não for um número válido, também redireciona para login
                session.remove("usuarioId");
                 throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
            }

            // Retorna o modelo (agora com userName, se encontrado) para o template "home.html"
            return model;
        } else {
            // Caso o usuarioId não seja encontrado na sessão, redireciona para a página de login
            throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
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
