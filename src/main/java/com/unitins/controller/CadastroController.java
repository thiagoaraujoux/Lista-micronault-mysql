package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import com.unitins.repository.exceptions.DuplicateEmailException; // Importa a exceção
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Controller("/cadastro")
public class CadastroController {

    private final UsuarioRepository usuarioRepository;

    public CadastroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Get // Método para exibir a página de cadastro
    @Produces(MediaType.TEXT_HTML)
    @View("cadastro") // Especifica o template Thymeleaf a ser renderizado (cadastro.html)
    public Map<String, Object> cadastroPage(@QueryValue Optional<String> erro, @QueryValue Optional<String> sucesso) { // Adiciona parâmetro para receber erro E sucesso
        Map<String, Object> model = new HashMap<>();
        // Verifica se o parâmetro 'erro' está presente na URL e adiciona uma mensagem ao modelo
        if (erro.isPresent() && erro.get().equals("email_duplicado")) {
            model.put("errorMessage", "Este email já está cadastrado. Tente fazer login ou use outro email.");
        }
        // O template HTML/JavaScript é responsável por verificar o parâmetro 'sucesso'
        // model.put("cadastroSucesso", sucesso.isPresent() && sucesso.get().equals("true")); // O JavaScript já faz isso lendo a URL
        return model; // Retorna o mapa para o template
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<String> cadastrar(@Body Map<String, String> form) {
        Usuario usuario = new Usuario();
        usuario.setNome(form.get("nome"));
        usuario.setEmail(form.get("email"));
        // Lembre-se de que a criptografia da senha já deve estar no UsuarioRepository.salvar
        usuario.setSenha(form.get("senha"));

        try {
            usuarioRepository.salvar(usuario);
            // Redireciona de volta para a página de cadastro, mas com o parâmetro de sucesso
            // O JavaScript na página detectará este parâmetro, exibirá a mensagem e fará o redirecionamento final
            try {
                 URI uri = new URI("/cadastro?sucesso=true"); // <-- Redireciona para a própria página com sucesso=true
                 return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }

        } catch (DuplicateEmailException e) {
            // Se o email for duplicado, redireciona de volta para /cadastro com um parâmetro de erro
            try {
                URI uri = new URI("/cadastro?erro=email_duplicado");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro.");
            }
        } catch (RuntimeException e) {
             // Para outros erros de banco de dados não tratados especificamente
             e.printStackTrace();
             // Você pode redirecionar para uma página de erro genérica ou adicionar um parâmetro de erro diferente
             return HttpResponse.serverError("Ocorreu um erro ao tentar cadastrar o usuário.");
        }
    }
}
