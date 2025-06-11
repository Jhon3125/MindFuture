package com.example.mindfuture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        long countByUsuario(Usuario usuario);

        @Query("SELECT COUNT(au) FROM ActividadUsuario au WHERE au.usuario = :usuario AND au.actividad.tipo = :tipo")
        long countByUsuarioAndActividadTipo(@Param("usuario") Usuario usuario,
                        @Param("tipo") MindfulnessActividad.TipoActividad tipo);

        Optional<ActividadUsuario> findByUsuarioAndActividad(Usuario usuario, MindfulnessActividad actividad);
}