package com.example.mindfuture.repository;

import com.example.mindfuture.model.Sesion;
import com.example.mindfuture.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {
    List<Sesion> findByUsuario(Usuario usuario);
}
