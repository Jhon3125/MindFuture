package com.example.mindfuture.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.mindfuture.model.ActividadUsuario;
import com.example.mindfuture.model.Logro;
import com.example.mindfuture.model.MindfulnessActividad;
import com.example.mindfuture.model.NivelMindfulness;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioLogro;
import com.example.mindfuture.model.UsuarioProgreso;
import com.example.mindfuture.repository.ActividadUsuarioRepository;
import com.example.mindfuture.repository.LogroRepository;
import com.example.mindfuture.repository.MindfulnessActividadRepository;
import com.example.mindfuture.repository.NivelMindfulnessRepository;
import com.example.mindfuture.repository.UsuarioLogrosRepository;
import com.example.mindfuture.repository.UsuarioProgresoRepository;
import com.example.mindfuture.services.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mindfulness")
public class MindfulnessController {

    private static final Logger log = LoggerFactory.getLogger(MindfulnessController.class);

    private final MindfulnessActividadRepository actividadRepository;
    private final UsuarioProgresoRepository progresoRepository;
    private final ActividadUsuarioRepository actividadUsuarioRepository;
    private final UsuarioLogrosRepository usuarioLogrosRepository;
    private final NivelMindfulnessRepository nivelRepository;
    private final LogroRepository logroRepository;

    public MindfulnessController(
            MindfulnessActividadRepository actividadRepository,
            UsuarioProgresoRepository progresoRepository,
            ActividadUsuarioRepository actividadUsuarioRepository,
            UsuarioLogrosRepository usuarioLogrosRepository,
            NivelMindfulnessRepository nivelRepository,
            LogroRepository logroRepository) {

        this.actividadRepository = actividadRepository;
        this.progresoRepository = progresoRepository;
        this.actividadUsuarioRepository = actividadUsuarioRepository;
        this.usuarioLogrosRepository = usuarioLogrosRepository;
        this.nivelRepository = nivelRepository;
        this.logroRepository = logroRepository;
    }

    @GetMapping("/mindfulness-game")
    public String showGamesPage(@AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails userDetails,
            Model model) {
        try {
            Usuario usuario = userDetails.getUsuario();

            // Obtener o crear progreso del usuario
            UsuarioProgreso progreso = progresoRepository.findByUsuario(usuario)
                    .orElseGet(() -> {
                        NivelMindfulness nivelInicial = nivelRepository.findByOrden(1)
                                .orElseThrow(() -> new RuntimeException("No se encontró el nivel inicial"));
                        UsuarioProgreso nuevoProgreso = new UsuarioProgreso();
                        nuevoProgreso.setUsuario(usuario);
                        nuevoProgreso.setNivel(nivelInicial);
                        return progresoRepository.save(nuevoProgreso);
                    });

            // Verificar y actualizar el nivel según las estrellas acumuladas
            NivelMindfulness nivelActual = progreso.getNivel();
            NivelMindfulness nuevoNivel = nivelRepository
                    .findByEstrellasRequeridasLessThanEqual(progreso.getEstrellasAcumuladas())
                    .stream()
                    .filter(n -> n.getOrden() > nivelActual.getOrden())
                    .max(Comparator.comparingInt(NivelMindfulness::getOrden))
                    .orElse(null);

            if (nuevoNivel != null) {
                progreso.setNivel(nuevoNivel);
                progreso.setCompletado(true);
                progreso.setFechaCompletado(new Date());
                progreso = progresoRepository.save(progreso);
                model.addAttribute("nuevoNivelDesbloqueado", true);
            }

            // Obtener nivel siguiente
            NivelMindfulness proximoNivel = nivelRepository.findByOrden(progreso.getNivel().getOrden() + 1)
                    .orElse(null);

            // Calcular porcentaje completado
            double porcentajeCompletado = calcularPorcentajeCompletado(progreso, progreso.getNivel(), proximoNivel);

            // Obtener actividades del nivel actual
            List<MindfulnessActividad> actividadesDisponibles = actividadRepository.findByNivel(progreso.getNivel())
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(a -> a.getNivel() != null)
                    .collect(Collectors.toList());

            // Obtener IDs de actividades completadas
            List<Integer> actividadesCompletadasIds = actividadUsuarioRepository
                    .findByUsuarioAndCompletado(usuario, true)
                    .stream()
                    .filter(au -> au != null && au.getActividad() != null)
                    .map(au -> au.getActividad().getIdActividad())
                    .collect(Collectors.toList());

            // Obtener logros del usuario
            List<UsuarioLogro> logrosUsuario = usuarioLogrosRepository.findByUsuario(usuario)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Agregar atributos al modelo
            model.addAttribute("nivelActual", progreso.getNivel().getNombre());
            model.addAttribute("proximoNivel",
                    proximoNivel != null ? proximoNivel.getNombre() : "¡Máximo nivel alcanzado!");
            model.addAttribute("porcentajeCompletado", porcentajeCompletado);
            model.addAttribute("estrellasTotales", progreso.getEstrellasAcumuladas());
            model.addAttribute("actividadesDisponibles", actividadesDisponibles);
            model.addAttribute("actividadesCompletadasIds", actividadesCompletadasIds);
            model.addAttribute("logrosUsuario", logrosUsuario);
            model.addAttribute("progreso", progreso);

        } catch (RuntimeException e) {
            log.error("Error al cargar la página de mindfulness game", e);
            model.addAttribute("error", "Ocurrió un error al cargar los datos: " + e.getMessage());
            model.addAttribute("actividadesDisponibles", Collections.emptyList());
            model.addAttribute("actividadesCompletadasIds", Collections.emptyList());
            model.addAttribute("logrosUsuario", Collections.emptyList());
        }

        return "mindfulness-game";
    }

    @GetMapping("/actividad/{id}")
    public String showActivityPage(@PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails userDetails,
            Model model,
            HttpServletRequest request) {

        // Obtener el token CSRF
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);

        try {
            MindfulnessActividad actividad = actividadRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

            model.addAttribute("actividad", actividad);

            // Determinar la vista basada en el nombre de la actividad
            String viewName = switch (actividad.getNombre().toLowerCase()) {
                case "respiración básica" -> "actividad/respiracion-basica";
                case "relajación guiada" -> "actividad/relajacion-guiada";
                default -> "actividad/generica";
            };

            return viewName;
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar la actividad");
            return "redirect:/mindfulness/mindfulness-game";
        }
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
    public ResponseEntity<Map<String, Object>> completarActividad(
            @RequestParam("idActividad") Integer idActividad,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        try {
            Usuario usuario = userDetails.getUsuario();
            MindfulnessActividad actividad = actividadRepository.findById(idActividad)
                    .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

            // 1. Buscar registro existente (completado o no)
            Optional<ActividadUsuario> registroExistente = actividadUsuarioRepository
                    .findByUsuarioAndActividad(usuario, actividad);

            boolean esPrimeraCompletacion = registroExistente.isEmpty() ||
                    !registroExistente.get().getCompletado();

            // 2. Guardar/Actualizar actividad usuario
            ActividadUsuario actividadUsuario;
            if (registroExistente.isPresent()) {
                actividadUsuario = registroExistente.get();
                actividadUsuario.setCompletado(true);
                actividadUsuario.setFechaRealizacion(new Date());
            } else {
                actividadUsuario = new ActividadUsuario();
                actividadUsuario.setUsuario(usuario);
                actividadUsuario.setActividad(actividad);
                actividadUsuario.setCompletado(true);
            }
            actividadUsuarioRepository.save(actividadUsuario);

            // 3. Manejar progreso y estrellas
            UsuarioProgreso progreso = progresoRepository.findByUsuarioAndNivel(usuario, actividad.getNivel())
                    .orElseGet(() -> {
                        UsuarioProgreso nuevoProgreso = new UsuarioProgreso();
                        nuevoProgreso.setUsuario(usuario);
                        nuevoProgreso.setNivel(actividad.getNivel());
                        return nuevoProgreso;
                    });

            int estrellasGanadas = actividad.getEstrellasRecompensa();
            int estrellasAAgregar = 0;

            if (esPrimeraCompletacion) {
                estrellasAAgregar = estrellasGanadas;
                progreso.setEstrellasAcumuladas(progreso.getEstrellasAcumuladas() + estrellasAAgregar);

                // Verificar si se completó el nivel
                if (!progreso.getCompletado() &&
                        progreso.getEstrellasAcumuladas() >= actividad.getNivel().getEstrellasRequeridas()) {
                    progreso.setCompletado(true);
                    progreso.setFechaCompletado(new Date());
                }

                progresoRepository.save(progreso);
            }

            // 4. Verificar logros
            List<Logro> logrosDesbloqueados = verificarLogros(usuario, actividad);

            // 5. Preparar respuesta
            response.put("success", true);
            response.put("estrellasGanadas", estrellasAAgregar);
            response.put("estrellasTotales", progreso.getEstrellasAcumuladas());
            response.put("logrosDesbloqueados", logrosDesbloqueados);
            response.put("esPrimeraCompletacion", esPrimeraCompletacion);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error al completar la actividad: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private List<Logro> verificarLogros(Usuario usuario, MindfulnessActividad actividad) {
        List<Logro> logrosDesbloqueados = new ArrayList<>();

        // 1. Logro por primera actividad completada
        long totalActividades = actividadUsuarioRepository.countByUsuarioAndCompletado(usuario, true);
        if (totalActividades == 1) {
            asignarLogroSiNoExiste(usuario, "COMPLETE_FIRST_ACTIVITY", logrosDesbloqueados);
        }

        // 2. Logro por completar actividades de cada tipo
        long countRespiracion = actividadUsuarioRepository.countByUsuarioAndActividadTipo(
                usuario, MindfulnessActividad.TipoActividad.respiracion);
        long countRelajacion = actividadUsuarioRepository.countByUsuarioAndActividadTipo(
                usuario, MindfulnessActividad.TipoActividad.relajacion);
        long countMeditacion = actividadUsuarioRepository.countByUsuarioAndActividadTipo(
                usuario, MindfulnessActividad.TipoActividad.meditacion);

        if (countRespiracion >= 3) {
            asignarLogroSiNoExiste(usuario, "COMPLETE_3_BREATHINGS", logrosDesbloqueados);
        }
        if (countRelajacion >= 3) {
            asignarLogroSiNoExiste(usuario, "COMPLETE_3_RELAXATIONS", logrosDesbloqueados);
        }
        if (countMeditacion >= 3) {
            asignarLogroSiNoExiste(usuario, "COMPLETE_3_MEDITATIONS", logrosDesbloqueados);
        }
        if (countRespiracion > 0 && countRelajacion > 0 && countMeditacion > 0) {
            asignarLogroSiNoExiste(usuario, "TRY_ALL_ACTIVITY_TYPES", logrosDesbloqueados);
        }

        // 3. Logro por estrellas acumuladas
        UsuarioProgreso progreso = progresoRepository.findByUsuarioAndNivel(usuario, actividad.getNivel())
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));

        if (progreso.getEstrellasAcumuladas() >= 10) {
            asignarLogroSiNoExiste(usuario, "EARN_10_STARS", logrosDesbloqueados);
        }

        // 4. Logro por completar todos los niveles (si es el último nivel)
        if (progreso.getCompletado()) {
            long nivelesCompletados = progresoRepository.countByUsuarioAndCompletado(usuario, true);
            long totalNiveles = nivelRepository.count();

            if (nivelesCompletados == totalNiveles) {
                asignarLogroSiNoExiste(usuario, "COMPLETE_ALL_LEVELS", logrosDesbloqueados);
            }
        }

        return logrosDesbloqueados;
    }

    private void asignarLogroSiNoExiste(Usuario usuario, String condicion, List<Logro> logrosDesbloqueados) {
        Logro logro = logroRepository.findByCondicion(condicion)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado para condición: " + condicion));

        boolean yaExiste = usuarioLogrosRepository.existsByUsuarioAndLogro(usuario, logro);

        if (!yaExiste) {
            UsuarioLogro usuarioLogro = new UsuarioLogro();
            usuarioLogro.setUsuario(usuario);
            usuarioLogro.setLogro(logro);
            usuarioLogrosRepository.save(usuarioLogro);
            logrosDesbloqueados.add(logro);
        }
    }
}