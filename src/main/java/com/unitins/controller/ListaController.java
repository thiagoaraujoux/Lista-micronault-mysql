package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.repository.ListaRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus; // Importe HttpStatus
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;

@Controller("/lista") // Este controller lida com todas as operações de /lista/*
public class ListaController {

    private final ListaRepository listaRepository;
    // private final UsuarioRepository usuarioRepository; // Descomente e injete no construtor se precisar

    // Construtor para injeção de dependência
    public ListaController(ListaRepository listaRepository /*, UsuarioRepository usuarioRepository*/) {
        this.listaRepository = listaRepository;
        // this.usuarioRepository = usuarioRepository; // Descomente se injetar
    }

    // Método para exibir a lista de listas
    // Mapeia para GET /lista
    @Get
    @Produces(MediaType.TEXT_HTML)
    // ATENÇÃO: Verifique se o caminho do template está correto
    @View("lista") // Ex: "entities/lista/list" se moveu
    public Map<String, Object> index(Session session) {
        Map<String, Object> model = new HashMap<>();

        // Verifica se o usuário está logado
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            // Redireciona para o login se não estiver logado
            throw new io.micronaut.http.exceptions.HttpStatusException(HttpStatus.SEE_OTHER, URI.create("/login"));
        }

        try {
            Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
            // Chama o método do repositório para buscar as listas do usuário logado
            List<Lista> listasDoUsuario = listaRepository.buscarPorUsuario(usuarioId);
            model.put("listas", listasDoUsuario); // Adiciona a lista de listas ao modelo
        } catch (NumberFormatException e) {
            // Lidar com ID de usuário inválido na sessão
            session.remove("usuarioId");
            throw new io.micronaut.http.exceptions.HttpStatusException(HttpStatus.SEE_OTHER, URI.create("/login"));
        } catch (Exception e) {
            // Lidar com outros possíveis erros ao carregar as listas (ex: erro de banco)
            e.printStackTrace();
            // Opcional: adicionar uma mensagem de erro ao modelo para exibir na tela
            // model.put("erro", "Não foi possível carregar suas listas.");
        }

        // Retorna o mapa de modelo, que será usado pelo template "lista.html"
        return model;
    }

    // Método para exibir o formulário de cadastro (se ainda usar rota GET direta)
    // Mapeia para GET /lista/cadastro
    @Get("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    // ATENÇÃO: Verifique se o caminho do template está correto
    @View("lista_cadastro") // Ex: "entities/lista/form" se moveu
    public Map<String, Object> showListaCadastroForm(Session session) {
        // Com o uso do modal no "lista.html", este método GET para /lista/cadastro
        // pode não ser mais a forma principal de exibir o formulário.
        // No entanto, ele ainda pode ser útil ou mantido por compatibilidade
        // se houver outras formas de acesso a este formulário.

        Map<String, Object> model = new HashMap<>();
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            throw new io.micronaut.http.exceptions.HttpStatusException(HttpStatus.SEE_OTHER, URI.create("/login"));
        }
        // Adicionar dados para o formulário, se necessário (ex: lista de categorias)
        // model.put("categorias", carregarCategorias());
        return model;
    }

    // Método para processar o cadastro de uma NOVA lista (requisição POST do modal)
    // Mapeia para POST /lista/cadastro
    @Post(value = "/cadastro", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    // Retorna HttpResponse<?> pois a requisição é AJAX (não renderiza view)
    public HttpResponse<?> cadastrarLista(@Body Map<String, String> form, Session session) {

        // Verifica se o usuário está logado
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            // Retorna 401 Unauthorized para a requisição AJAX
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        // Cria e popula o objeto Lista com os dados do formulário
        Lista lista = new Lista();
        lista.setTitulo(form.get("titulo"));
        lista.setDescricao(form.get("descricao"));

        // Associa a lista ao usuário logado obtido da sessão
        try {
            Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
            lista.setUsuarioId(usuarioId);
        } catch (NumberFormatException e) {
            // Lidar com ID de usuário inválido na sessão
            e.printStackTrace();
            session.remove("usuarioId");
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        // Associa a categoria, se fornecida
        String categoriaIdStr = form.get("categoriaId");
        if (categoriaIdStr != null && !categoriaIdStr.trim().isEmpty()) {
            try {
                lista.setCategoriaId(Long.parseLong(categoriaIdStr));
            } catch (NumberFormatException e) {
                // Lidar com ID de categoria inválido no formulário
                e.printStackTrace();
                return HttpResponse.badRequest("ID da categoria inválido.");
            }
        } else {
            lista.setCategoriaId(null); // Define como null se o campo estiver vazio
        }

        try {
            // Salva a nova lista usando o repositório
            listaRepository.salvar(lista);
            // Retorna resposta de sucesso (ex: 201 Created ou 200 OK) para a requisição AJAX
            return HttpResponse.created(URI.create("/lista")); // 201 Created
            // Ou: return HttpResponse.ok("Lista cadastrada com sucesso."); // 200 OK
        } catch (RuntimeException e) {
            // Lidar com erros ao salvar (ex: erro de banco de dados)
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao salvar a lista: " + e.getMessage()); // Retorna 500 Internal Server Error
        }
    }


    // Método para processar a EDIÇÃO de uma lista existente (requisição POST do modal)
    // Mapeia para POST /lista/editar/{id}
    @Post(value = "/editar/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    // Retorna HttpResponse<?> pois a requisição é AJAX (não renderiza view)
    public HttpResponse<?> editarLista(@PathVariable Long id, @Body Map<String, String> form, Session session) {

        // Verifica se o usuário está logado
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED); // Retorna 401 Unauthorized
        }

        try {
            // Busca a lista existente pelo ID
            Optional<Lista> listaOptional = listaRepository.findById(id);

            // Verifica se a lista foi encontrada
            if (listaOptional.isEmpty()) {
                return HttpResponse.notFound("Lista com ID " + id + " não encontrada para edição."); // Retorna 404 Not Found
            }

            Lista listaParaAtualizar = listaOptional.get();

            // Opcional (MAS RECOMENDADO): Verificar se a lista pertence ao usuário logado
            Long usuarioIdLogado = Long.parseLong(usuarioIdOptional.get().toString());
            if (!listaParaAtualizar.getUsuarioId().equals(usuarioIdLogado)) {
                // Se a lista não pertence ao usuário, retorna 403 Forbidden
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão para editar esta lista.");
            }

            // Atualiza os dados da lista com base nos dados do formulário
            listaParaAtualizar.setTitulo(form.get("titulo"));
            listaParaAtualizar.setDescricao(form.get("descricao"));

            String categoriaIdStr = form.get("categoriaId");
            if (categoriaIdStr != null && !categoriaIdStr.trim().isEmpty()) {
                try {
                    listaParaAtualizar.setCategoriaId(Long.parseLong(categoriaIdStr));
                } catch (NumberFormatException e) {
                    // Lidar com ID de categoria inválido no formulário
                    e.printStackTrace();
                    return HttpResponse.badRequest("ID da categoria inválido.");
                }
            } else {
                listaParaAtualizar.setCategoriaId(null); // Define como null se o campo estiver vazio
            }

            // Chama o método de atualização no repositório
            listaRepository.atualizar(listaParaAtualizar); // Usa o método atualizar

            // Retorna resposta de sucesso (ex: 200 OK) para a requisição AJAX
            return HttpResponse.ok("Lista atualizada com sucesso."); // 200 OK

        } catch (NumberFormatException e) {
            // Lidar com ID de usuário inválido na sessão
            e.printStackTrace();
            session.remove("usuarioId");
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            // Lidar com erros ao atualizar (ex: erro de banco de dados)
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao atualizar a lista: " + e.getMessage());
        }
    }


    // Método para processar a EXCLUSÃO de uma lista (requisição POST do JavaScript)
    // Mapeia para POST /lista/excluir/{id}
    // NOTA: Usar POST para exclusão via AJAX é comum, mas DELETE para /lista/{id} seria mais RESTful.
    @Post("/excluir/{id}")
    // Retorna HttpResponse<?> pois a requisição é AJAX (não renderiza view)
    public HttpResponse<?> excluirLista(@PathVariable Long id, Session session) {

        // Verifica se o usuário está logado
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED); // Retorna 401 Unauthorized
        }

        try {
            // Opcional (MAS RECOMENDADO): Verificar se a lista pertence ao usuário logado antes de excluir
            // Para isso, você precisa buscar a lista primeiro para verificar o usuarioId
            Optional<Lista> listaOptional = listaRepository.findById(id);
            if (listaOptional.isEmpty()) {
                // Se a lista não existe, retorna 404 Not Found
                return HttpResponse.notFound("Lista com ID " + id + " não encontrada para exclusão.");
            }
            Lista listaParaExcluir = listaOptional.get();

            Long usuarioIdLogado = Long.parseLong(usuarioIdOptional.get().toString());
            if (!listaParaExcluir.getUsuarioId().equals(usuarioIdLogado)) {
                // Se a lista não pertence ao usuário, retorna 403 Forbidden
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir esta lista.");
            }

            // Chama o método de exclusão no repositório
            listaRepository.excluir(id); // Usa o método excluir

            // Retorna resposta de sucesso (ex: 200 OK ou 204 No Content) para a requisição AJAX
            return HttpResponse.ok("Lista excluída com sucesso."); // 200 OK
            // Ou: return HttpResponse.noContent(); // 204 No Content
        } catch (NumberFormatException e) {
             // Lidar com ID de usuário inválido na sessão
             e.printStackTrace();
             session.remove("usuarioId");
             return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            // Lidar com erros ao excluir (ex: erro de banco de dados)
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao excluir a lista: " + e.getMessage());
        }
    }

    // Você pode adicionar outros métodos para outras operações relacionadas a Lista aqui (ex: ver detalhes de uma lista)

    // Método placeholder para carregar categorias (se precisar no futuro)
    // private List<Categoria> carregarCategorias() { ... }
}