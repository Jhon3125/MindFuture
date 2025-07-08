package com.example.mindfuture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/vistaejemplo")
    public String mostrarVistaEjemplo() {
        return "admin/vistaejemplo";
    }
}
