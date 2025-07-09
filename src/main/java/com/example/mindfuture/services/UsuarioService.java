package com.example.mindfuture.services;

import com.example.mindfuture.model.Usuario;
import java.util.List;
import java.util.Map;

public interface UsuarioService {
    Usuario findByEmail(String email);
    Usuario findByIdAndEmail(Object idUsuario, String emailUsuario);
    long countUsuarios();
    Map<String, Long> countByTipoSuscripcion();
    List<Usuario> findUsuariosByRol(Usuario.Rol rol);
}
