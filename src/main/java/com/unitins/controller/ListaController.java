package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.repository.ListaRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;

import java.net.URI;
import java.util.*;

@Controller("/lista")
public class ListaController {

    private final ListaRepository listaRepository;

    public ListaController(ListaRepository listaRepository) {
        this.listaRepository = listaRepository;
    }

    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("lista")
    public Map<String, Object> index(Session session) {
        Map<String, Object> model = new HashMap<>();

        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            model.put("redirect", "/login");
            return model;
        }

        try {
            Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
            List<Lista> listasDoUsuario = listaRepository.buscarPorUsuario(usuarioId);
            model.put("listas", listasDoUsuario);
        } catch (NumberFormatException e) {
            session.remove("usuarioId");
            model.put("redirect", "/login");
        } catch (Exception e) {
            e.printStackTrace();
            model.put("erro", "Erro ao carregar suas listas.");
        }

        return model;
    }

    @Get("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    @View("lista_cadastro")
    public Map<String, Object> showListaCadastroForm(Session session) {
        Map<String, Object> model = new HashMap<>();
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            model.put("redirect", "/login");
        }
        return model;
    }

    @Post(value = "/cadastro", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cadastrarLista(@Body Map<String, String> form, Session session) {
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        Lista lista = new Lista();
        lista.setTitulo(form.get("titulo"));
        lista.setDescricao(form.get("descricao"));

        try {
            Long usuarioId = Long.parseLong(usuarioIdOptional.get().toString());
            lista.setUsuarioId(usuarioId);
        } catch (NumberFormatException e) {
            session.remove("usuarioId");
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        String categoriaIdStr = form.get("categoriaId");
        if (categoriaIdStr != null && !categoriaIdStr.trim().isEmpty()) {
            try {
                lista.setCategoriaId(Long.parseLong(categoriaIdStr));
            } catch (NumberFormatException e) {
                return HttpResponse.badRequest("ID da categoria inválido.");
            }
        } else {
            lista.setCategoriaId(null);
        }

        try {
            listaRepository.salvar(lista);
            return HttpResponse.created(URI.create("/lista"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao salvar a lista: " + e.getMessage());
        }
    }

    @Post(value = "/editar/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> editarLista(@PathVariable Long id, @Body Map<String, String> form, Session session) {
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        try {
            Optional<Lista> listaOptional = listaRepository.findById(id);
            if (listaOptional.isEmpty()) {
                return HttpResponse.notFound("Lista com ID " + id + " não encontrada.");
            }

            Lista listaParaAtualizar = listaOptional.get();
            Long usuarioIdLogado = Long.parseLong(usuarioIdOptional.get().toString());
            if (!listaParaAtualizar.getUsuarioId().equals(usuarioIdLogado)) {
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão.");
            }

            listaParaAtualizar.setTitulo(form.get("titulo"));
            listaParaAtualizar.setDescricao(form.get("descricao"));

            String categoriaIdStr = form.get("categoriaId");
            if (categoriaIdStr != null && !categoriaIdStr.trim().isEmpty()) {
                try {
                    listaParaAtualizar.setCategoriaId(Long.parseLong(categoriaIdStr));
                } catch (NumberFormatException e) {
                    return HttpResponse.badRequest("ID da categoria inválido.");
                }
            } else {
                listaParaAtualizar.setCategoriaId(null);
            }

            listaRepository.atualizar(listaParaAtualizar);
            return HttpResponse.ok("Lista atualizada com sucesso.");
        } catch (NumberFormatException e) {
            session.remove("usuarioId");
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao atualizar a lista: " + e.getMessage());
        }
    }

    @Post("/excluir/{id}")
    public HttpResponse<?> excluirLista(@PathVariable Long id, Session session) {
        Optional<Object> usuarioIdOptional = session.get("usuarioId");
        if (usuarioIdOptional.isEmpty()) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        }

        try {
            Optional<Lista> listaOptional = listaRepository.findById(id);
            if (listaOptional.isEmpty()) {
                return HttpResponse.notFound("Lista com ID " + id + " não encontrada.");
            }

            Lista listaParaExcluir = listaOptional.get();
            Long usuarioIdLogado = Long.parseLong(usuarioIdOptional.get().toString());
            if (!listaParaExcluir.getUsuarioId().equals(usuarioIdLogado)) {
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão.");
            }

            listaRepository.excluir(id);
            return HttpResponse.ok("Lista excluída com sucesso.");
        } catch (NumberFormatException e) {
            session.remove("usuarioId");
            return HttpResponse.status(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro ao excluir a lista: " + e.getMessage());
        }
    }
}
