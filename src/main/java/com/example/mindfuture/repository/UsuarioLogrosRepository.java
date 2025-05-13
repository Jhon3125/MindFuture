package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.UsuarioLogro;

@Repository
public interface UsuarioLogrosRepository extends JpaRepository<UsuarioLogro, Integer> {
    List<UsuarioLogro> findByUsuario(Usuario usuario);
}