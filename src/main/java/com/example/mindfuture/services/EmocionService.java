package com.example.mindfuture.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.mindfuture.model.Emocion;
import com.example.mindfuture.repository.EmocionRepository;

@Service
public class EmocionService {

    private final EmocionRepository emocionRepository;

    public EmocionService(EmocionRepository emocionRepository) {
        this.emocionRepository = emocionRepository;
    }

    public Emocion registrar(Emocion emocion) {
        return emocionRepository.save(emocion);
    }

    public List<Emocion> listarPorUsuario(Integer idUsuario) {
        return emocionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public void save(Emocion emocion) {
        emocionRepository.save(emocion);
    }

}
