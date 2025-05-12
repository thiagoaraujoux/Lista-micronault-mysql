package com.unitins.controller;

import com.unitins.model.Categoria;
import com.unitins.repository.CategoriaRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; // Importa Optional

@Controller("/categoria/cadastro")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    // Injeta o CategoriaRepository
    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Get // Método para exibir o formulário de cadastro de categoria
    @Produces(MediaType.TEXT_HTML)
    @View("categoria_cadastro") // Especifica o template a ser renderizado (categoria_cadastro.html)
    public Map<String, Object> showCategoriaCadastroForm(@QueryValue Optional<String> erro, @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();

        // Adiciona mensagens de erro/sucesso ao modelo se presentes nos parâmetros da URL
        if (erro.isPresent()) {
            // Você pode adicionar lógica para diferentes tipos de erro aqui
             model.put("errorMessage", "Erro ao cadastrar categoria. Tente novamente.");
        }
         if (sucesso.isPresent() && sucesso.get().equals("true")) {
             model.put("successMessage", "Categoria cadastrada com sucesso!");
         }


        return model; // Retorna o mapa para o template "categoria_cadastro.html"
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cadastrarCategoria(@Body Map<String, String> form) {

        // Cria um objeto Categoria com os dados do formulário
        Categoria categoria = new Categoria();
        categoria.setNome(form.get("nome")); // Assume que o campo se chama 'nome' no formulário

        try {
            // Salva a categoria usando o repositório
            categoriaRepository.salvar(categoria);

            // Redireciona de volta para a página de cadastro com um parâmetro de sucesso
            // O JavaScript no template pode detectar isso e exibir uma mensagem
            try {
                 URI uri = new URI("/categoria/cadastro?sucesso=true");
                 return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }

        } catch (RuntimeException e) {
             // Para erros de banco de dados ao salvar a categoria
             e.printStackTrace();
             // Redirecionar de volta para o formulário com mensagem de erro
             try {
                 URI uri = new URI("/categoria/cadastro?erro=salvar");
                 return HttpResponse.redirect(uri);
             } catch (URISyntaxException ex) {
                  ex.printStackTrace();
                  return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro.");
             }
        }
    }

    // Você pode adicionar outros métodos aqui (ex: para listar categorias)
}
