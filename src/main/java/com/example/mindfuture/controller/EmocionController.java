package com.example.mindfuture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mindfuture.dto.EmocionDTO;
import com.example.mindfuture.model.Emocion;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.services.EmocionService;
import com.example.mindfuture.services.UsuarioService;

@RestController
@RequestMapping("/api/emociones")
public class EmocionController {

    @Autowired
    private EmocionService emocionService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/api/emociones")
    public ResponseEntity<?> registrarEmocion(@RequestBody EmocionDTO dto) {
        Usuario usuario = usuarioService.findByEmail(dto.getEmailUsuario());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Emocion emocion = new Emocion();
        emocion.setUsuario(usuario);
        emocion.setEmocionDetectada(dto.getEmocionDetectada());
        emocion.setNivelEstres(dto.getNivelEstres());
        emocion.setRecomendacion(dto.getRecomendacion());

        try {
            Emocion.Fuente fuenteEnum = Emocion.Fuente.valueOf(dto.getFuente().toLowerCase());
            emocion.setFuente(fuenteEnum);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Fuente inválida");
        }

        emocionService.registrar(emocion);
        return ResponseEntity.ok("Emoción registrada");
    }
}