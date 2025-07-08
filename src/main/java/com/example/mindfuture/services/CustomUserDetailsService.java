package com.example.mindfuture.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.mindfuture.model.Usuario;
import com.example.mindfuture.model.Usuario.TipoSuscripcion;
import com.example.mindfuture.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new CustomUserDetails(usuario);
    }

    // Clase interna mejorada
    public static class CustomUserDetails implements UserDetails {
        private final Usuario usuario;

        public CustomUserDetails(Usuario usuario) {
            this.usuario = usuario;
        }

        // Método para acceder al usuario completo
        public Usuario getUsuario() {
            return usuario;
        }

        // Métodos específicos para acceder a propiedades importantes
        public Integer getIdUsuario() {
            return usuario.getIdUsuario();
        }

        public String getNombre() {
            return usuario.getNombre();
        }

        public TipoSuscripcion getTipoSuscripcion() {
            return usuario.getTipoSuscripcion();
        }

        public Date getFechaPremium() {
            return usuario.getFechaPremium();
        }

        public Usuario.Rol getRol() {
            return usuario.getRol();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
        }

        @Override
        public String getPassword() {
            return usuario.getContraseña();
        }

        @Override
        public String getUsername() {
            return usuario.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}