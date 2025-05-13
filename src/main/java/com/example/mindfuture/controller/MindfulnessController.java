package com.example.mindfuture.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.mindfuture.model.MindfulnessActividad;
import com.example.mindfuture.model.NivelMindfulness;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioLogro;
import com.example.mindfuture.model.UsuarioProgreso;
import com.example.mindfuture.repository.ActividadUsuarioRepository;
import com.example.mindfuture.repository.MindfulnessActividadRepository;
import com.example.mindfuture.repository.NivelMindfulnessRepository;
import com.example.mindfuture.repository.UsuarioLogrosRepository;
import com.example.mindfuture.repository.UsuarioProgresoRepository;
import com.example.mindfuture.services.CustomUserDetailsService;

@Controller
public class MindfulnessController {

        private final MindfulnessActividadRepository actividadRepository;
        private final UsuarioProgresoRepository progresoRepository;
        private final ActividadUsuarioRepository actividadUsuarioRepository;
        private final UsuarioLogrosRepository usuarioLogrosRepository;
        private final NivelMindfulnessRepository nivelRepository;

        public MindfulnessController(MindfulnessActividadRepository actividadRepository,
                        UsuarioProgresoRepository progresoRepository,
                        ActividadUsuarioRepository actividadUsuarioRepository,
                        UsuarioLogrosRepository usuarioLogrosRepository,
                        NivelMindfulnessRepository nivelRepository) {
                this.actividadRepository = actividadRepository;
                this.progresoRepository = progresoRepository;
                this.actividadUsuarioRepository = actividadUsuarioRepository;
                this.usuarioLogrosRepository = usuarioLogrosRepository;
                this.nivelRepository = nivelRepository;
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
}