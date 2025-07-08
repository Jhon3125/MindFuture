package com.example.mindfuture.controller;

import com.example.mindfuture.dto.CrearSesionDTO;
import com.example.mindfuture.model.Sesion;
import com.example.mindfuture.model.Terapeuta;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.services.SesionService;
import com.example.mindfuture.repository.TerapeutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/therapy")
public class TherapyController {

    private final TerapeutaRepository terapeutaRepository;
    private final SesionService sesionService;

    @GetMapping("/vr")
    public String mostrarVistaVR(@AuthenticationPrincipal(expression = "usuario") Usuario usuario, Model model) {
        List<Terapeuta> therapists = terapeutaRepository.findAll();
        List<Sesion> sessions = sesionService.obtenerSesionesPorUsuario(usuario);

        model.addAttribute("therapists", therapists);
        model.addAttribute("sessions", sessions);

        return "vr-therapy";
    }

    @PostMapping("/session")
    public String agendarSesion(@AuthenticationPrincipal(expression = "usuario") Usuario usuario,
                                @ModelAttribute CrearSesionDTO dto) {
        sesionService.crearSesionDesdeFormulario(dto, usuario);
        return "redirect:/therapy/vr";
    }
}
