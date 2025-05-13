package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.MindfulnessActividad;
import com.example.mindfuture.model.NivelMindfulness;

@Repository
public interface MindfulnessActividadRepository extends JpaRepository<MindfulnessActividad, Integer> {
    // Cambiar de findByNivel_Id a findByNivel para coincidir con la relación
    List<MindfulnessActividad> findByNivel(NivelMindfulness nivel);

    // O si necesitas buscar por ID del nivel
    List<MindfulnessActividad> findByNivelId(Integer nivelId);
}