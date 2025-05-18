package com.example.mindfuture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.Logro;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Integer> {
    Optional<Logro> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    Optional<Logro> findByCondicion(String condicion);
}