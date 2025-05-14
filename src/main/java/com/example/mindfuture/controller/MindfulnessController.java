package com.example.mindfuture.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.mindfuture.model.*;
import com.example.mindfuture.repository.*;
import com.example.mindfuture.services.CustomUserDetailsService;

import jakarta.transaction.Transactional;

@Controller
public class MindfulnessController {

        private final MindfulnessActividadRepository actividadRepository;
        private final UsuarioProgresoRepository progresoRepository;
        private final ActividadUsuarioRepository actividadUsuarioRepository;
        private final UsuarioLogrosRepository usuarioLogrosRepository;
        private final NivelMindfulnessRepository nivelRepository;
        private final LogroRepository logroRepository;
        private final UsuarioSesionesDiariasRepository sesionesDiariasRepository;

        public MindfulnessController(
                        MindfulnessActividadRepository actividadRepository,
                        UsuarioProgresoRepository progresoRepository,
                        ActividadUsuarioRepository actividadUsuarioRepository,
                        UsuarioLogrosRepository usuarioLogrosRepository,
                        NivelMindfulnessRepository nivelRepository,
                        LogroRepository logroRepository,
                        UsuarioSesionesDiariasRepository sesionesDiariasRepository) {

                this.actividadRepository = actividadRepository;
                this.progresoRepository = progresoRepository;
                this.actividadUsuarioRepository = actividadUsuarioRepository;
                this.usuarioLogrosRepository = usuarioLogrosRepository;
                this.nivelRepository = nivelRepository;
                this.logroRepository = logroRepository;
                this.sesionesDiariasRepository = sesionesDiariasRepository;
        }

        @GetMapping("/mindfulness-game")
        public String showGamesPage(@AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails userDetails,
                        Model model) {
                try {
                        Usuario usuario = userDetails.getUsuario();

                        // Obtener progreso del usuario
                        UsuarioProgreso progreso = progresoRepository.findByUsuario(usuario)
                                        .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));

                        // Obtener nivel actual y siguiente
                        NivelMindfulness nivelActual = progreso.getNivel();
                        NivelMindfulness proximoNivel = nivelRepository.findByOrden(nivelActual.getOrden() + 1)
                                        .orElse(null);

                        // Calcular porcentaje completado
                        double porcentajeCompletado = calcularPorcentajeCompletado(progreso, nivelActual, proximoNivel);

                        // Obtener actividades disponibles (filtradas para evitar nulls)
                        List<MindfulnessActividad> actividadesDisponibles = actividadRepository.findByNivel(nivelActual)
                                        .stream()
                                        .filter(a -> a != null)
                                        .collect(Collectors.toList());

                        // Obtener IDs de actividades completadas (manejo seguro)
                        List<Integer> actividadesCompletadasIds = actividadUsuarioRepository
                                        .findByUsuarioAndCompletado(usuario, true)
                                        .stream()
                                        .filter(au -> au != null && au.getActividad() != null)
                                        .map(au -> au.getActividad().getIdActividad())
                                        .collect(Collectors.toList());

                        // Obtener logros del usuario (manejo seguro)
                        List<UsuarioLogro> logrosUsuario = usuarioLogrosRepository.findByUsuario(usuario)
                                        .stream()
                                        .filter(ul -> ul != null)
                                        .collect(Collectors.toList());

                        // Agregar atributos al modelo
                        model.addAttribute("nivelActual", nivelActual.getNombre());
                        model.addAttribute("proximoNivel",
                                        proximoNivel != null ? proximoNivel.getNombre() : "¡Máximo nivel alcanzado!");
                        model.addAttribute("porcentajeCompletado", porcentajeCompletado);
                        model.addAttribute("estrellasTotales", progreso.getEstrellasAcumuladas());
                        model.addAttribute("actividadesDisponibles", actividadesDisponibles);
                        model.addAttribute("actividadesCompletadasIds", actividadesCompletadasIds);
                        model.addAttribute("logrosUsuario", logrosUsuario);

                } catch (Exception e) {
                        // Manejo básico de errores
                        model.addAttribute("error", "Ocurrió un error al cargar los datos");
                        model.addAttribute("actividadesDisponibles", Collections.emptyList());
                        model.addAttribute("actividadesCompletadasIds", Collections.emptyList());
                        model.addAttribute("logrosUsuario", Collections.emptyList());
                }

                return "mindfulness-game";
        }

        private double calcularPorcentajeCompletado(UsuarioProgreso progreso, NivelMindfulness nivelActual,
                        NivelMindfulness proximoNivel) {
                if (proximoNivel == null) {
                        return 100.0;
                }

                int estrellasNecesarias = proximoNivel.getEstrellasRequeridas() - nivelActual.getEstrellasRequeridas();
                int estrellasObtenidas = progreso.getEstrellasAcumuladas() - nivelActual.getEstrellasRequeridas();

                return estrellasNecesarias > 0
                                ? Math.min(100.0,
                                                Math.max(0.0, ((double) estrellasObtenidas / estrellasNecesarias)
                                                                * 100))
                                : 0.0;
        }

        @PostMapping("/completar-actividad")
        @ResponseBody
        @Transactional
        public ResponseEntity<?> completarActividad(
                        @RequestBody Map<String, Object> payload,
                        @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails userDetails) {

                try {
                        // Validación básica mejorada
                        if (!validarPayloadActividad(payload)) {
                                return ResponseEntity.badRequest().body(Map.of(
                                                "success", false,
                                                "message", "Datos de actividad inválidos"));
                        }

                        Usuario usuario = userDetails.getUsuario();
                        Integer actividadId = (Integer) payload.get("actividadId");
                        Integer estrellasRecompensa = (Integer) payload.get("estrellas");

                        // Obtener la actividad con manejo de errores
                        MindfulnessActividad actividad = actividadRepository.findById(actividadId)
                                        .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

                        // Verificar si ya está completada (evitar duplicados)
                        if (actividadUsuarioRepository.existsByUsuarioAndActividadAndCompletado(usuario, actividad,
                                        true)) {
                                return ResponseEntity.ok().body(Map.of(
                                                "success", true,
                                                "message", "Actividad ya estaba completada",
                                                "alreadyCompleted", true));
                        }

                        // 1. Registrar actividad completada
                        ActividadUsuario actividadUsuario = new ActividadUsuario();
                        actividadUsuario.setUsuario(usuario);
                        actividadUsuario.setActividad(actividad);
                        actividadUsuario.setCompletado(true);
                        actividadUsuarioRepository.save(actividadUsuario);

                        // 2. Registrar sesión diaria
                        registrarSesionDiaria(usuario);

                        // 3. Actualizar progreso del usuario
                        UsuarioProgreso progreso = progresoRepository.findByUsuario(usuario)
                                        .orElseGet(() -> {
                                                UsuarioProgreso nuevoProgreso = new UsuarioProgreso();
                                                nuevoProgreso.setUsuario(usuario);
                                                nuevoProgreso.setNivel(actividad.getNivel());
                                                nuevoProgreso.setEstrellasAcumuladas(0);
                                                nuevoProgreso.setCompletado(false);
                                                return progresoRepository.save(nuevoProgreso);
                                        });

                        progreso.setEstrellasAcumuladas(progreso.getEstrellasAcumuladas() + estrellasRecompensa);
                        progresoRepository.save(progreso);

                        // 4. Verificar si se completó el nivel (todas las actividades)
                        boolean nivelCompletado = verificarCompletitudNivel(usuario, progreso);

                        // 5. Evaluar logros
                        List<String> nuevosLogros = evaluarLogrosUsuario(usuario, actividad);

                        // Construir respuesta
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Actividad completada exitosamente");
                        response.put("estrellasGanadas", estrellasRecompensa);
                        response.put("estrellasTotales", progreso.getEstrellasAcumuladas());
                        response.put("nivelCompletado", nivelCompletado);

                        if (!nuevosLogros.isEmpty()) {
                                response.put("nuevosLogros", nuevosLogros);
                        }

                        return ResponseEntity.ok().body(response);

                } catch (Exception e) {
                        return ResponseEntity.internalServerError().body(Map.of(
                                        "success", false,
                                        "message", "Error al completar la actividad: " + e.getMessage()));
                }
        }

        // Métodos auxiliares mejorados

        private boolean validarPayloadActividad(Map<String, Object> payload) {
                try {
                        Integer actividadId = (Integer) payload.get("actividadId");
                        Integer estrellas = (Integer) payload.get("estrellas");
                        return actividadId != null && actividadId > 0 && estrellas != null && estrellas >= 0;
                } catch (ClassCastException e) {
                        return false;
                }
        }

        private void registrarSesionDiaria(Usuario usuario) {
                LocalDate hoy = LocalDate.now();
                Date fecha = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());

                UsuarioSesionesDiarias sesionDiaria = sesionesDiariasRepository.findByUsuarioAndFecha(usuario, fecha)
                                .orElseGet(() -> {
                                        UsuarioSesionesDiarias nueva = new UsuarioSesionesDiarias();
                                        nueva.setUsuario(usuario);
                                        nueva.setFecha(fecha);
                                        nueva.setSesionesCompletadas(0);
                                        return nueva;
                                });

                sesionDiaria.setSesionesCompletadas(sesionDiaria.getSesionesCompletadas() + 1);
                sesionesDiariasRepository.save(sesionDiaria);
        }

        private boolean verificarCompletitudNivel(Usuario usuario, UsuarioProgreso progreso) {
                // Obtener todas las actividades del nivel actual
                List<MindfulnessActividad> actividadesNivel = actividadRepository.findByNivel(progreso.getNivel());

                // Obtener IDs de actividades completadas por el usuario en este nivel
                Set<Integer> actividadesCompletadasIds = actividadUsuarioRepository
                                .findByUsuarioAndCompletado(usuario, true)
                                .stream()
                                .map(au -> au.getActividad().getIdActividad())
                                .collect(Collectors.toSet());

                // Verificar si todas las actividades del nivel están completadas
                boolean todasCompletadas = actividadesNivel.stream()
                                .allMatch(a -> actividadesCompletadasIds.contains(a.getIdActividad()));

                if (todasCompletadas && !progreso.getCompletado()) {
                        progreso.setCompletado(true);
                        progreso.setFechaCompletado(new Date());
                        progresoRepository.save(progreso);

                        // Avanzar al siguiente nivel si existe
                        nivelRepository.findByOrden(progreso.getNivel().getOrden() + 1)
                                        .ifPresent(siguienteNivel -> {
                                                UsuarioProgreso nuevoProgreso = new UsuarioProgreso();
                                                nuevoProgreso.setUsuario(usuario);
                                                nuevoProgreso.setNivel(siguienteNivel);
                                                nuevoProgreso.setEstrellasAcumuladas(0);
                                                nuevoProgreso.setCompletado(false);
                                                progresoRepository.save(nuevoProgreso);
                                        });

                        return true;
                }

                return false;
        }

        private List<String> evaluarLogrosUsuario(Usuario usuario, MindfulnessActividad actividadActual) {
                List<String> logrosDesbloqueados = new ArrayList<>();

                // Datos necesarios para evaluar logros
                long totalActividades = actividadUsuarioRepository.countByUsuarioAndCompletado(usuario, true);
                int totalEstrellas = progresoRepository.findByUsuario(usuario)
                                .map(UsuarioProgreso::getEstrellasAcumuladas)
                                .orElse(0);

                // 1. Primeros pasos (primera actividad completada)
                if (totalActividades == 1) {
                        otorgarLogroSiNoExiste(usuario, "COMPLETE_FIRST_ACTIVITY", logrosDesbloqueados);
                }

                // 2. Explorador (todos los tipos de actividades)
                if (haCompletadoTodosTiposActividades(usuario)) {
                        otorgarLogroSiNoExiste(usuario, "TRY_ALL_ACTIVITY_TYPES", logrosDesbloqueados);
                }

                // 3. Estrella naciente (10 estrellas)
                if (totalEstrellas >= 10) {
                        otorgarLogroSiNoExiste(usuario, "EARN_10_STARS", logrosDesbloqueados);
                }

                // 4. Logros específicos por tipo de actividad
                switch (actividadActual.getTipo()) {
                        case meditacion:
                                long meditacionesCompletadas = actividadUsuarioRepository
                                                .findByUsuarioAndCompletado(usuario, true)
                                                .stream()
                                                .filter(au -> au.getActividad()
                                                                .getTipo() == MindfulnessActividad.TipoActividad.meditacion)
                                                .count();

                                if (meditacionesCompletadas >= 10) {
                                        otorgarLogroSiNoExiste(usuario, "COMPLETE_10_MEDITATIONS", logrosDesbloqueados);
                                }
                                break;

                        case relajacion:
                                // Similar para relajación
                                break;

                        case respiracion:
                                // Similar para respiración
                                break;
                }

                // 5. Días consecutivos (implementar según tu lógica)
                if (tieneDiasConsecutivos(usuario, 3)) {
                        otorgarLogroSiNoExiste(usuario, "THREE_CONSECUTIVE_DAYS", logrosDesbloqueados);
                }

                return logrosDesbloqueados;
        }

        private void otorgarLogroSiNoExiste(Usuario usuario, String condicion, List<String> logrosDesbloqueados) {
                logroRepository.findByCondicion(condicion).ifPresent(logro -> {
                        if (!usuarioLogrosRepository.existsByUsuarioAndLogro(usuario, logro)) {
                                UsuarioLogro usuarioLogro = new UsuarioLogro();
                                usuarioLogro.setUsuario(usuario);
                                usuarioLogro.setLogro(logro);
                                usuarioLogrosRepository.save(usuarioLogro);
                                logrosDesbloqueados.add(logro.getNombre());
                        }
                });
        }

        private boolean haCompletadoTodosTiposActividades(Usuario usuario) {
                Set<MindfulnessActividad.TipoActividad> tiposCompletados = actividadUsuarioRepository
                                .findByUsuarioAndCompletado(usuario, true)
                                .stream()
                                .map(au -> au.getActividad().getTipo())
                                .collect(Collectors.toSet());

                return tiposCompletados.containsAll(EnumSet.allOf(MindfulnessActividad.TipoActividad.class));
        }

        private boolean tieneDiasConsecutivos(Usuario usuario, int diasRequeridos) {
                // Implementación básica - debes adaptarla a tu lógica
                // Consulta las sesiones diarias y verifica si hay 'diasRequeridos' consecutivos
                List<UsuarioSesionesDiarias> sesiones = sesionesDiariasRepository.findByUsuarioOrderByFechaDesc(usuario);

                // Lógica para verificar días consecutivos
                // ...

                return false; // Cambiar por tu implementación real
        }

}