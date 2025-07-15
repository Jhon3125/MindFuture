package com.example.mindfuture.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Usuario findByIdAndEmail(Object idUsuario, String emailUsuario) {
        return usuarioRepository.findByIdUsuarioAndEmail((Long) idUsuario, emailUsuario).orElse(null);
    }

    @Override
    public long countUsuarios() {
        return usuarioRepository.count();
    }

@Override
public Map<String, Long> countByTipoSuscripcion() {
    return usuarioRepository.findAll().stream()
        .collect(Collectors.groupingBy(
            u -> u.getTipoSuscripcion().name(), // aquí se convierte el enum a String
            Collectors.counting()
        ));
}

    @Override
    public List<Usuario> findUsuariosByRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
}
