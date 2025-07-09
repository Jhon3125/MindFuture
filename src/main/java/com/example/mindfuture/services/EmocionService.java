package com.example.mindfuture.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.mindfuture.model.Emocion;
import com.example.mindfuture.repository.EmocionRepository;

@Service
public class EmocionService {

    private final EmocionRepository emocionRepository;

    public EmocionService(EmocionRepository emocionRepository) {
        this.emocionRepository = emocionRepository;
    }

    public Emocion registrar(Emocion emocion) {
        return emocionRepository.save(emocion);
    }

    public List<Emocion> listarPorUsuario(Integer idUsuario) {
        return emocionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public void save(Emocion emocion) {
        emocionRepository.save(emocion);
    }

    public Optional<Emocion> buscarPorUsuarioYFecha(Integer idUsuario, Date fechaInicio, Date fechaFin) {
        return emocionRepository.findTopByUsuarioIdUsuarioAndFechaRegistroBetween(idUsuario, fechaInicio, fechaFin);
    }

    // ✅ Nuevo método para obtener emoción por usuario y fecha
    public Optional<Emocion> obtenerPorUsuarioYFecha(Integer idUsuario, Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);

        // Establecer hora inicial del día
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date fechaInicio = cal.getTime();

        // Establecer hora final del día
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date fechaFin = cal.getTime();

        return emocionRepository.findTopByUsuarioIdUsuarioAndFechaRegistroBetween(idUsuario, fechaInicio, fechaFin);
    }
}
