package com.example.mindfuture.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.mindfuture.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final NivelMindfulnessRepository nivelMindfulnessRepository;
    private final MindfulnessActividadRepository actividadRepository;
    private final ActividadUsuarioRepository actividadUsuarioRepository;
    private final UsuarioProgresoRepository progresoRepository;
    private final LogroRepository logroRepository;
    private final UsuarioLogrosRepository usuarioLogrosRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
            NivelMindfulnessRepository nivelMindfulnessRepository,
            MindfulnessActividadRepository actividadRepository,
            ActividadUsuarioRepository actividadUsuarioRepository,
            UsuarioProgresoRepository progresoRepository,
            PasswordEncoder passwordEncoder,
            LogroRepository logroRepository,
            UsuarioLogrosRepository usuarioLogrosRepository) {
        this.usuarioRepository = usuarioRepository;
        this.nivelMindfulnessRepository = nivelMindfulnessRepository;
        this.actividadRepository = actividadRepository;
        this.actividadUsuarioRepository = actividadUsuarioRepository;
        this.progresoRepository = progresoRepository;
        this.passwordEncoder = passwordEncoder;
        this.logroRepository = logroRepository;
        this.usuarioLogrosRepository = usuarioLogrosRepository;
    }

    @Transactional
    public void registerNewUser(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setRol(Usuario.Rol.usuario);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        inicializarDatosUsuario(usuarioGuardado);
        asignarLogroBienvenida(usuarioGuardado);
    }

    private void asignarLogroBienvenida(Usuario usuario) {
        Logro logroBienvenida = logroRepository.findByCondicion("FIRST_LOGIN")
                .orElseThrow(() -> new RuntimeException("Logro de bienvenida no configurado"));

        UsuarioLogro usuarioLogro = new UsuarioLogro();
        usuarioLogro.setUsuario(usuario);
        usuarioLogro.setLogro(logroBienvenida);
        usuarioLogrosRepository.save(usuarioLogro);
    }

    private void inicializarDatosUsuario(Usuario usuario) {
        if (nivelMindfulnessRepository.count() == 0) {
            crearNivelesIniciales();
        }

        asignarProgresoInicial(usuario);
        inicializarActividadesUsuario(usuario);
    }

    private void crearNivelesIniciales() {
        // Nivel Principiante
        NivelMindfulness nivel1 = new NivelMindfulness();
        nivel1.setNombre("Principiante");
        nivel1.setDescripcion("Nivel inicial para nuevos usuarios");
        nivel1.setEstrellasRequeridas(0);
        nivel1.setOrden(1);
        nivelMindfulnessRepository.save(nivel1);

        // Nivel Intermedio
        NivelMindfulness nivel2 = new NivelMindfulness();
        nivel2.setNombre("Intermedio");
        nivel2.setDescripcion("Nivel para usuarios con experiencia básica");
        nivel2.setEstrellasRequeridas(10);
        nivel2.setOrden(2);
        nivelMindfulnessRepository.save(nivel2);

        // Nivel Avanzado
        NivelMindfulness nivel3 = new NivelMindfulness();
        nivel3.setNombre("Avanzado");
        nivel3.setDescripcion("Nivel para usuarios experimentados");
        nivel3.setEstrellasRequeridas(25);
        nivel3.setOrden(3);
        nivelMindfulnessRepository.save(nivel3);

        crearActividadesIniciales(nivel1);
    }

    private void crearActividadesIniciales(NivelMindfulness nivel) {
        // Actividad 1 - Respiración
        MindfulnessActividad actividad1 = new MindfulnessActividad();
        actividad1.setNivel(nivel);
        actividad1.setNombre("Respiración básica");
        actividad1.setEstrellasRecompensa(3);
        actividad1.setTipo(MindfulnessActividad.TipoActividad.respiracion);
        actividad1.setDescripcion("Ejercicio básico de respiración para principiantes");
        actividad1.setDuracionEstimada(5);
        actividadRepository.save(actividad1);

        // Actividad 2 - Relajación
        MindfulnessActividad actividad2 = new MindfulnessActividad();
        actividad2.setNivel(nivel);
        actividad2.setNombre("Relajación guiada");
        actividad2.setEstrellasRecompensa(3);
        actividad2.setTipo(MindfulnessActividad.TipoActividad.relajacion);
        actividad2.setDescripcion("Relajación guiada para reducir el estrés");
        actividad2.setDuracionEstimada(10);
        actividadRepository.save(actividad2);
    }

    private void asignarProgresoInicial(Usuario usuario) {
        NivelMindfulness nivelInicial = nivelMindfulnessRepository.findAll()
                .stream()
                .min(Comparator.comparing(NivelMindfulness::getOrden))
                .orElseThrow(() -> new RuntimeException("No se encontraron niveles"));

        UsuarioProgreso progreso = new UsuarioProgreso();
        progreso.setUsuario(usuario);
        progreso.setNivel(nivelInicial);
        progreso.setEstrellasAcumuladas(0);
        progreso.setCompletado(false);
        progresoRepository.save(progreso);
    }

    private void inicializarActividadesUsuario(Usuario usuario) {
        NivelMindfulness nivelInicial = nivelMindfulnessRepository.findAll()
                .stream()
                .min(Comparator.comparing(NivelMindfulness::getOrden))
                .orElseThrow(() -> new RuntimeException("No se encontraron niveles"));

        List<MindfulnessActividad> actividades = actividadRepository.findByNivel(nivelInicial);

        actividades.forEach(actividad -> {
            ActividadUsuario actividadUsuario = new ActividadUsuario();
            actividadUsuario.setUsuario(usuario);
            actividadUsuario.setActividad(actividad);
            actividadUsuario.setCompletado(false);
            actividadUsuarioRepository.save(actividadUsuario);
        });
    }
}