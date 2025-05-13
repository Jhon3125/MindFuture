package com.example.mindfuture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioProgreso;

@Repository
public interface UsuarioProgresoRepository extends JpaRepository<UsuarioProgreso, Integer> {
    Optional<UsuarioProgreso> findByUsuario(Usuario usuario);
}