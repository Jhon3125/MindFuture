package com.example.mindfuture.services;

import java.util.Optional;

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
        Optional<Usuario> optional = usuarioRepository.findByEmail(email);
        return optional.orElse(null); // o puedes lanzar una excepción si prefieres
    }

    @Override
    public Usuario findByIdAndEmail(Object idUsuario, String emailUsuario) {
        // Esto es opcional, según si tienes ese método en el repositorio
        return null; // puedes implementar esto si es necesario
    }
}
