package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View; // Importa a anotação View

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap; // Importa HashMap

@Controller("/cadastro")
public class CadastroController {

    private final UsuarioRepository usuarioRepository;

    public CadastroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Get // Método para exibir a página de cadastro
    @Produces(MediaType.TEXT_HTML)
    @View("cadastro") // Especifica o template Thymeleaf a ser renderizado (cadastro.html)
    public Map<String, Object> cadastroPage() { // <-- Alterado o tipo de retorno para Map
        // Retorna um mapa vazio. Micronaut Views usará a anotação @View para encontrar o template.
        return new HashMap<>();
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<String> cadastrar(@Body Map<String, String> form) {
        Usuario usuario = new Usuario();
        usuario.setNome(form.get("nome"));
        usuario.setEmail(form.get("email"));
        // Lembre-se de criptografar a senha antes de salvar em produção!
        usuario.setSenha(form.get("senha"));

        usuarioRepository.salvar(usuario);

        try {
            // Redireciona para a página de login após o cadastro
            URI uri = new URI("/login");
            return HttpResponse.redirect(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao construir URI de redirecionamento.");
        }
    }
}
