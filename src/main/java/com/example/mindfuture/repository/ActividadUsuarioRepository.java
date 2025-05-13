package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.ActividadUsuario;
import com.example.mindfuture.model.Usuario;

@Repository
public interface ActividadUsuarioRepository extends JpaRepository<ActividadUsuario, Integer> {
    List<ActividadUsuario> findByUsuarioAndCompletado(Usuario usuario, Boolean completado);
}