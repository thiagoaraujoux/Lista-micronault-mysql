package com.unitins.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import io.micronaut.session.Session; // Importa Session
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; // Importa Optional

@Controller("/lista")
public class ListaController {

    // Você pode injetar o UsuarioRepository aqui se precisar buscar o usuário logado
    // private final UsuarioRepository usuarioRepository;
    // public ListaController(UsuarioRepository usuarioRepository) {
    //     this.usuarioRepository = usuarioRepository;
    // }

    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("lista") // Especifica o template Thymeleaf a ser renderizado (lista.html)
    public Map<String, Object> index(Session session) { // Adiciona Session para verificar autenticação
        Map<String, Object> model = new HashMap<>();

        // Exemplo básico de verificação de autenticação
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
             // Se o usuário não estiver logado, redireciona para o login
             // Use HttpStatusException para redirecionar dentro de um método @View
             throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, java.net.URI.create("/login"));
        }

        // Adicionar dados ao modelo, se necessário (ex: listas do usuário)
        // model.put("listas", carregarListasDoUsuario(usuarioIdOptional.get()));

        return model; // Retorna o mapa para o template "lista.html"
    }

    // Método placeholder para carregar listas (implementar depois)
    // private List<Lista> carregarListasDoUsuario(Object usuarioId) {
    //     // Implementar a busca das listas do usuário no banco de dados
    //     return Collections.emptyList(); // Retorna lista vazia por enquanto
    // }

    // Você pode adicionar outros métodos aqui (ex: para exibir uma lista específica)
}
