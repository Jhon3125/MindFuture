package com.example.mindfuture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Página principal accesible sin login
    @GetMapping({ "/", "/index" })
    public String home(Model model) {
        return "index";
    }

    // Otras rutas protegidas (requieren login)
    @GetMapping("/vr-therapy")
    public String vrTherapy() {
        return "vr-therapy";
    }

    @GetMapping("/mood-tracker")
    public String moodTracker() {
        return "mood-tracker";
    }
}