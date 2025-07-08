package com.example.mindfuture.services;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubscriptionService {

    private final UsuarioRepository usuarioRepository;

    public SubscriptionService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public void upgradeToPremium() {
        Usuario usuario = getCurrentUser();
        if (usuario != null && usuario.getTipoSuscripcion() == Usuario.TipoSuscripcion.gratis) {
            usuario.setTipoSuscripcion(Usuario.TipoSuscripcion.premium);
            usuario.setFechaPremium(new Date());
            usuarioRepository.save(usuario);
        }
    }

    public boolean isPremium() {
        Usuario usuario = getCurrentUser();
        return usuario != null && usuario.getTipoSuscripcion() == Usuario.TipoSuscripcion.premium;
    }
}