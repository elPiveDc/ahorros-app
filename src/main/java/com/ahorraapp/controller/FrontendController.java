package com.ahorraapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/ahorrapp")
@Controller
public class FrontendController {

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @GetMapping("/gastos/registrar")
    public String registrarGasto() {
        return "registro_gasto";
    }

    @GetMapping("/gastos/analisis")
    public String analisis() {
        return "analisis_gastos";
    }

    @GetMapping("/tienda")
    public String tienda() {
        return "tienda";
    }

    @GetMapping("/logros_metas")
    public String logrosymetas() {
        return "Logros_Metas";
    }

}
