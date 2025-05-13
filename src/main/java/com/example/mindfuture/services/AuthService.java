package com.example.mindfuture.services;

import java.util.Comparator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mindfuture.model.Logro;
import com.example.mindfuture.model.MindfulnessActividad;
import com.example.mindfuture.model.NivelMindfulness;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioLogro;
import com.example.mindfuture.model.UsuarioProgreso;
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
    private final UsuarioProgresoRepository progresoRepository;
    private final LogroRepository logroRepository;
    private final UsuarioLogrosRepository usuarioLogrosRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
            NivelMindfulnessRepository nivelMindfulnessRepository,
            MindfulnessActividadRepository actividadRepository,
            UsuarioProgresoRepository progresoRepository,
            PasswordEncoder passwordEncoder,
            LogroRepository logroRepository,
            UsuarioLogrosRepository usuarioLogrosRepository) {
        this.usuarioRepository = usuarioRepository;
        this.nivelMindfulnessRepository = nivelMindfulnessRepository;
        this.actividadRepository = actividadRepository;
        this.progresoRepository = progresoRepository;
        this.passwordEncoder = passwordEncoder;
        this.logroRepository = logroRepository;
        this.usuarioLogrosRepository = usuarioLogrosRepository;
    }

    @Transactional
    public void registerNewUser(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Configurar valores por defecto
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setRol(Usuario.Rol.usuario);

        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Inicializar datos relacionados
        inicializarDatosUsuario(usuarioGuardado);

        asignarLogroBienvenida(usuarioGuardado);
    }

    private void asignarLogroBienvenida(Usuario usuario) {
        // Buscar el logro de bienvenida por su condición
        Logro logroBienvenida = logroRepository.findByCondicion("FIRST_LOGIN")
                .orElseThrow(() -> new RuntimeException("Logro de bienvenida no configurado en la base de datos"));

        // Crear y guardar la relación usuario-logro
        UsuarioLogro usuarioLogro = new UsuarioLogro();
        usuarioLogro.setUsuario(usuario);
        usuarioLogro.setLogro(logroBienvenida);
        usuarioLogrosRepository.save(usuarioLogro);
    }

    private void inicializarDatosUsuario(Usuario usuario) {
        // Crear niveles de mindfulness iniciales si no existen
        if (nivelMindfulnessRepository.count() == 0) {
            crearNivelesIniciales();
        }

        // Asignar progreso inicial al usuario
        asignarProgresoInicial(usuario);
    }

    private void crearNivelesIniciales() {
        NivelMindfulness nivel1 = new NivelMindfulness();
        nivel1.setNombre("Principiante");
        nivel1.setDescripcion("Nivel inicial para nuevos usuarios");
        nivel1.setEstrellasRequeridas(0);
        nivel1.setOrden(1);
        nivelMindfulnessRepository.save(nivel1);

        NivelMindfulness nivel2 = new NivelMindfulness();
        nivel2.setNombre("Intermedio");
        nivel2.setDescripcion("Nivel para usuarios con algo de experiencia");
        nivel2.setEstrellasRequeridas(10);
        nivel2.setOrden(2);
        nivelMindfulnessRepository.save(nivel2);

        NivelMindfulness nivel3 = new NivelMindfulness();
        nivel3.setNombre("Avanzado");
        nivel3.setDescripcion("Nivel para usuarios experimentados");
        nivel3.setEstrellasRequeridas(25);
        nivel3.setOrden(3);
        nivelMindfulnessRepository.save(nivel3);

        // Crear actividades iniciales
        crearActividadesIniciales(nivel1);
    }

    private void crearActividadesIniciales(NivelMindfulness nivel) {
        MindfulnessActividad actividad1 = new MindfulnessActividad();
        actividad1.setNivel(nivel);
        actividad1.setNombre("Respiración básica");
        actividad1.setEstrellasRecompensa(1);
        actividad1.setTipo(MindfulnessActividad.TipoActividad.respiracion);
        actividad1.setDescripcion("Ejercicio básico de respiración para principiantes");
        actividad1.setDuracionEstimada(5);
        actividadRepository.save(actividad1);

        MindfulnessActividad actividad2 = new MindfulnessActividad();
        actividad2.setNivel(nivel);
        actividad2.setNombre("Relajación guiada");
        actividad2.setEstrellasRecompensa(2);
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
}