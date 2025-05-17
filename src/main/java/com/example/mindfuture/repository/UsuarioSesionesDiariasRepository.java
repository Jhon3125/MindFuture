package com.example.mindfuture.repository;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioSesionesDiarias;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioSesionesDiariasRepository extends JpaRepository<UsuarioSesionesDiarias, Integer> {

    // Busca una sesión diaria por usuario y fecha
    Optional<UsuarioSesionesDiarias> findByUsuarioAndFecha(Usuario usuario, Date fecha);

    List<UsuarioSesionesDiarias> findByUsuarioOrderByFechaDesc(Usuario usuario);
}