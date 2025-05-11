package com.example.mindfuture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class home {

    // Página principal
    @GetMapping("/")
    public String home() {
        return "index"; // Carga src/main/resources/templates/index.html
    }

    // Módulo VR Therapy
    @GetMapping("/vr-therapy")
    public String vrTherapy() {
        return "vr-therapy"; // Carga vr-therapy.html
    }

    // Módulo Mood Tracker
    @GetMapping("/mood-tracker")
    public String moodTracker() {
        return "mood-tracker";
    }

    // Módulo Mindfulness Game
    @GetMapping("/mindfulness-game")
    public String mindfulnessGame() {
        return "mindfulness-game";
    }
}