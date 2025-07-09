package com.example.mindfuture.services;


import com.example.mindfuture.model.Usuario;
public interface UsuarioService {
    Usuario findByEmail(String email);
    Usuario findByIdAndEmail(Object idUsuario, String emailUsuario);
}
