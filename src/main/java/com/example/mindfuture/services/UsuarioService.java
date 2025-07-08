package com.example.mindfuture.services;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public long countUsuarios() {
        return usuarioRepository.count();
    }

    public Map<String, Long> countByTipoSuscripcion() {
        return usuarioRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        u -> u.getTipoSuscripcion().name(),
                        Collectors.counting()
                ));
    }

    public List<Usuario> findUsuariosByRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
}