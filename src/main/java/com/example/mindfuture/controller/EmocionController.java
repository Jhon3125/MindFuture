package com.example.mindfuture.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.mindfuture.dto.EmocionDTO;
import com.example.mindfuture.dto.EmocionRespuestaDTO;
import com.example.mindfuture.model.Emocion;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.repository.EmocionRepository;
import com.example.mindfuture.services.EmocionService;
import com.example.mindfuture.services.UsuarioService;

@RestController
@RequestMapping("/api/emociones")
public class EmocionController {

    @Autowired
    private EmocionService emocionService;

    @Autowired
    private EmocionRepository emocionRepository;

    @Autowired
    private UsuarioService usuarioService;

    // ✅ POST: registrar o actualizar emoción del día
    @PostMapping
    public ResponseEntity<?> registrarEmocion(@RequestBody EmocionDTO dto) {
        try {
            Usuario usuario = usuarioService.findByEmail(dto.getEmailUsuario());
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado: " + dto.getEmailUsuario());
            }

            // Fecha actual: desde las 00:00 hasta 23:59:59.999
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date fechaInicio = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date fechaFin = calendar.getTime();

            Optional<Emocion> existente = emocionRepository.findTopByUsuarioIdUsuarioAndFechaRegistroBetween(
                    usuario.getIdUsuario(), fechaInicio, fechaFin);

            Emocion emocion;
            if (existente.isPresent()) {
                emocion = existente.get();
                emocion.setEmocionDetectada(dto.getEmocionDetectada());
                emocion.setNivelEstres(dto.getNivelEstres());
                emocion.setRecomendacion(dto.getRecomendacion());
            } else {
                emocion = new Emocion();
                emocion.setUsuario(usuario);
                emocion.setEmocionDetectada(dto.getEmocionDetectada());
                emocion.setNivelEstres(dto.getNivelEstres());
                emocion.setRecomendacion(dto.getRecomendacion());
                emocion.setFechaRegistro(new Date());
            }

            emocionService.save(emocion);
            return ResponseEntity.ok("✅ Emoción registrada o actualizada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error interno: " + e.getMessage());
        }
    }

    // ✅ GET: obtener emoción por fecha y email
    @GetMapping
    public ResponseEntity<?> obtenerEmocionPorFechaYEmail(
            @RequestParam String email,
            @RequestParam String fecha) {
        try {
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // Parsear la fecha
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = sdf.parse(fecha);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaInicio);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date inicioDelDia = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date finDelDia = calendar.getTime();

            Optional<Emocion> emocionOpt = emocionRepository
                    .findTopByUsuarioIdUsuarioAndFechaRegistroBetween(
                            usuario.getIdUsuario(), inicioDelDia, finDelDia);

            if (emocionOpt.isPresent()) {
                Emocion emocion = emocionOpt.get();

                EmocionRespuestaDTO dto = new EmocionRespuestaDTO();
                dto.setEmocionDetectada(emocion.getEmocionDetectada());
                dto.setNivelEstres(emocion.getNivelEstres());
                dto.setRecomendacion(emocion.getRecomendacion());
                dto.setFechaRegistro(emocion.getFechaRegistro());

                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(404).body("No se encontró emoción para ese día");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @GetMapping("/grafico")
    public ResponseEntity<?> obtenerDatosGrafico(Authentication auth) {
        try {
            String email = auth.getName(); // Usuario autenticado
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // Obtener los últimos 7 registros ordenados por fecha
            List<Emocion> emociones = emocionRepository
                    .findByUsuarioIdUsuarioOrderByFechaRegistroAsc(usuario.getIdUsuario());

            // Transformar los datos para el gráfico
            List<Map<String, Object>> datosGrafico = emociones.stream().map(e -> {
                Map<String, Object> punto = new HashMap<>();
                punto.put("dia", new SimpleDateFormat("EEE").format(e.getFechaRegistro())); // Ej: "Lun"
                punto.put("nivelEstres", e.getNivelEstres());
                return punto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(datosGrafico);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error interno: " + e.getMessage());
        }
    }

}