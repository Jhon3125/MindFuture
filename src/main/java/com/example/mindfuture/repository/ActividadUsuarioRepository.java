package com.example.mindfuture.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mindfuture.model.ActividadUsuario;
import com.example.mindfuture.model.Usuario;



public interface ActividadUsuarioRepository extends JpaRepository<ActividadUsuario, Integer> {
    @Query("SELECT COUNT(au) FROM ActividadUsuario au WHERE au.usuario.id = :usuarioId AND au.completado = true AND au.fechaRealizacion > :fecha")
    Long contarActividadesCompletadasDespuesDeFecha(
            @Param("usuarioId") Integer usuarioId,
            @Param("fecha") Timestamp fecha);

    List<ActividadUsuario> findByIdUsuario(Usuario usuario);

    List<ActividadUsuario> findByIdUsuarioAndCompletadoTrue(Usuario usuario);
}