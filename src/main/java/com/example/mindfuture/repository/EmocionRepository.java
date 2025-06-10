package com.example.mindfuture.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.mindfuture.model.Emocion;

@Repository
public interface EmocionRepository extends JpaRepository<Emocion, Integer> {
    List<Emocion> findByUsuarioIdUsuario(Integer idUsuario);
}
