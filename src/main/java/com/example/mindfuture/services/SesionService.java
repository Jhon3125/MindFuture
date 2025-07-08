package com.example.mindfuture.services;

import com.example.mindfuture.dto.CrearSesionDTO;
import com.example.mindfuture.model.Sesion;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.Terapeuta;
import com.example.mindfuture.repository.SesionRepository;
import com.example.mindfuture.repository.TerapeutaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final TerapeutaRepository terapeutaRepository;

    private static final String ZOOM_URL = "https://utpvirtual.zoom.us/j/85402408961?pwd=rycKhG8Y5uaum0iwW4hIHhWLuDJwdA.1";

    public List<Sesion> obtenerSesionesPorUsuario(Usuario usuario) {
        return sesionRepository.findByUsuario(usuario);
    }

    public long countSesionesProgramadas() {
        return sesionRepository.countByFechaAfter(LocalDateTime.now());
    }

    public long countSesionesByTipo(Sesion.TipoSesion tipo) {
        return sesionRepository.countByTipo(tipo);
    }

    @Transactional
    public void crearSesionDesdeFormulario(CrearSesionDTO dto, Usuario usuario) {
        Terapeuta terapeuta = terapeutaRepository.findById(dto.getTherapistId())
                .orElseThrow(() -> new IllegalArgumentException("Terapeuta no encontrado"));

        LocalDateTime fechaHora = LocalDateTime.of(dto.getDate(), dto.getTime());
        Date fecha = Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant());

        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        sesion.setTerapeuta(terapeuta);
        sesion.setTipo(Sesion.TipoSesion.Zoom);
        sesion.setFecha(fecha);
        sesion.setDuracionMinutos(dto.getDuration());
        sesion.setUrl(ZOOM_URL);

        sesionRepository.save(sesion);
    }
}
