package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.Logro;
import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioLogro;

@Repository
public interface UsuarioLogrosRepository extends JpaRepository<UsuarioLogro, Integer> {
    // Verifica si un usuario tiene un logro específico
    boolean existsByUsuarioAndLogro(Usuario usuario, Logro logro);

    // Encuentra todos los logros de un usuario
    List<UsuarioLogro> findByUsuario(Usuario usuario);
}