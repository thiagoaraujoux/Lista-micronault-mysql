package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.repository.ListaRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session; // Importa Session

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; // Importa Optional

@Controller("/lista/cadastro")
public class ListaCadastroController {

    private final ListaRepository listaRepository;

    // Injeta o ListaRepository
    public ListaCadastroController(ListaRepository listaRepository) {
        this.listaRepository = listaRepository;
    }

    @Get // Método para exibir o formulário de cadastro de lista
    @Produces(MediaType.TEXT_HTML)
    @View("lista_cadastro") // Especifica o template a ser renderizado (lista_cadastro.html)
    public Map<String, Object> showListaCadastroForm(Session session) {
        Map<String, Object> model = new HashMap<>();

        // Verifica se o usuário está logado antes de exibir o formulário
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
             // Se não estiver logado, redireciona para o login
             throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
        }

        // Você pode adicionar dados ao modelo aqui, se necessário
        // Ex: carregar categorias para um dropdown
        // model.put("categorias", carregarCategorias());

        return model; // Retorna o mapa para o template "lista_cadastro.html"
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cadastrarLista(@Body Map<String, String> form, Session session) {

        // Verifica se o usuário está logado antes de processar o cadastro
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
             // Se não estiver logado, redireciona para o login
             throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
        }

        // Cria um objeto Lista com os dados do formulário
        Lista lista = new Lista();
        lista.setTitulo(form.get("titulo"));
        lista.setDescricao(form.get("descricao"));

        // Obtém o ID do usuário logado da sessão
        try {
            Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
            lista.setUsuarioId(usuarioId);
        } catch (NumberFormatException e) {
             e.printStackTrace();
             // Se o ID do usuário na sessão for inválido, redireciona para login
             session.remove("usuarioId"); // Limpa a sessão inválida
             throw new io.micronaut.http.exceptions.HttpStatusException(io.micronaut.http.HttpStatus.SEE_OTHER, URI.create("/login"));
        }


        // Obtém o ID da categoria do formulário (pode ser null)
        String categoriaIdStr = form.get("categoriaId"); // Assumindo que o campo se chama categoriaId
        if (categoriaIdStr != null && !categoriaIdStr.trim().isEmpty()) {
            try {
                lista.setCategoriaId(Long.parseLong(categoriaIdStr));
            } catch (NumberFormatException e) {
                 // Tratar erro se o ID da categoria não for um número válido
                 e.printStackTrace();
                 return HttpResponse.redirect(URI.create("/lista/cadastro?erro=categoria_invalida"));
            }
        } else {
            lista.setCategoriaId(null); // Define categoriaId como null se não fornecido
        }


        try {
            // Salva a lista usando o repositório
            listaRepository.salvar(lista);

            // Redireciona para a página principal de listas após o cadastro bem-sucedido
            try {
                 URI uri = new URI("/lista?sucesso=cadastro"); // Adiciona parâmetro de sucesso para a tela de listas
                 return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                 ex.printStackTrace();
                 return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }

        } catch (RuntimeException e) {
             // Para erros de banco de dados ao salvar a lista
             e.printStackTrace();
             return HttpResponse.redirect(URI.create("/lista/cadastro?erro=salvar_lista"));
        }
    }

    // Método placeholder para carregar categorias (implementar depois)
    // private List<Categoria> carregarCategorias() {
    //      // Implementar a busca de categorias no banco de dados
    //      return Collections.emptyList();
    // }
}
