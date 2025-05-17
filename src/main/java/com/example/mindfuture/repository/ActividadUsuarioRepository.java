package com.example.mindfuture.repository;

import java.util.List;

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

        boolean existsByUsuarioAndActividadIdActividad(Usuario usuario, Integer idActividad);

        boolean existsByUsuarioAndActividadTipo(Usuario usuario, MindfulnessActividad.TipoActividad tipo);

        @Query("SELECT COUNT(a) FROM ActividadUsuario a WHERE a.usuario = :usuario AND a.actividad.tipo = :tipo AND a.completado = :completado")
        long countByUsuarioAndActividadTipoAndCompletado(
                        @Param("usuario") Usuario usuario,
                        @Param("tipo") MindfulnessActividad.TipoActividad tipo,
                        @Param("completado") boolean completado);

        // Método alternativo más simple (si solo necesitas contar completadas)
        long countByUsuarioAndActividadTipoAndCompletadoTrue(
                        Usuario usuario,
                        MindfulnessActividad.TipoActividad tipo);

        boolean existsByUsuarioAndActividadTipoAndCompletado(Usuario usuario,
                        MindfulnessActividad.TipoActividad tipo, boolean completado);

        boolean existsByUsuarioAndActividadAndCompletado(Usuario usuario, MindfulnessActividad actividad,
                        Boolean completado);
}