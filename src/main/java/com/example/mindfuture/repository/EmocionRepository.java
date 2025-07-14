package com.example.mindfuture.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.mindfuture.model.Emocion;

@Repository
public interface EmocionRepository extends JpaRepository<Emocion, Integer> {

    List<Emocion> findByUsuarioIdUsuario(Integer idUsuario);

    Optional<Emocion> findTopByUsuarioIdUsuarioAndFechaRegistroBetween(
            Integer idUsuario,
            Date fechaInicio,
            Date fechaFin);

    List<Emocion> findByUsuarioIdUsuarioAndFechaRegistroBetweenOrderByFechaRegistroAsc(
            Integer idUsuario,
            Date desde,
            Date hasta);

    List<Emocion> findByUsuarioIdUsuarioOrderByFechaRegistroAsc(Integer idUsuario);
}
