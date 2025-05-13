package com.example.mindfuture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioProgreso;

@Repository
public interface UsuarioProgresoRepository extends JpaRepository<UsuarioProgreso, Integer> {
    @Query("SELECT up FROM UsuarioProgreso up WHERE up.usuario.id = :usuarioId")
    List<UsuarioProgreso> buscarPorUsuarioId(@Param("usuarioId") Integer usuarioId);

    List<UsuarioProgreso> findByIdUsuario(Usuario usuario);

    Optional<UsuarioProgreso> findByIdUsuarioAndIdNivel(Usuario usuario, Integer idNivel);

    Integer sumEstrellasAcumuladasByIdUsuario(Usuario usuario);
}