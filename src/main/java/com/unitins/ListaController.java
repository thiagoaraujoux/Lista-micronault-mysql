package com.unitins;

import io.micronaut.http.annotation.*;

@Controller("/lista")
public class ListaController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}