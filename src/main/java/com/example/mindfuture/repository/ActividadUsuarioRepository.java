package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.ActividadUsuario;
import com.example.mindfuture.model.MindfulnessActividad;
import com.example.mindfuture.model.Usuario;

@Repository
public interface ActividadUsuarioRepository extends JpaRepository<ActividadUsuario, Integer> {
    // Verifica si existe una actividad completada por un usuario específico
    boolean existsByUsuarioAndActividadAndCompletado(Usuario usuario, MindfulnessActividad actividad,
            boolean completado);

    // Cuenta las actividades completadas por un usuario
    long countByUsuarioAndCompletado(Usuario usuario, boolean completado);

    // Encuentra todas las actividades de un usuario filtradas por estado de
    // completado
    List<ActividadUsuario> findByUsuarioAndCompletado(Usuario usuario, boolean completado);
}