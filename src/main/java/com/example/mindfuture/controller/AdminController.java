package com.example.mindfuture.controller;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.services.SesionService;
import com.example.mindfuture.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SesionService sesionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Total de usuarios registrados
        long totalUsuarios = usuarioService.countUsuarios();
        model.addAttribute("totalUsuarios", totalUsuarios);

        // Total de sesiones programadas
        long totalSesiones = sesionService.countSesionesProgramadas();
        model.addAttribute("totalSesiones", totalSesiones);

        // Proporción de usuarios premium vs gratuitos
        Map<String, Long> suscripciones = usuarioService.countByTipoSuscripcion();
        model.addAttribute("suscripciones", suscripciones);

        // Lista de usuarios normales (no admin ni terapeutas)
        List<Usuario> usuariosNormales = usuarioService.findUsuariosByRol(Usuario.Rol.usuario);
        model.addAttribute("usuariosNormales", usuariosNormales);

        return "admin/dashboard";
    }
}