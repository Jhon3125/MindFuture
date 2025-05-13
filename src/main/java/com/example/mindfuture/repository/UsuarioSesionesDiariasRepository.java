package com.example.mindfuture.repository;

import com.example.mindfuture.model.UsuarioSesionesDiarias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioSesionesDiariasRepository extends JpaRepository<UsuarioSesionesDiarias, Integer> {
    // Métodos personalizados si son necesarios
}