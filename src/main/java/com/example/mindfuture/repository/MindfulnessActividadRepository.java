package com.example.mindfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.MindfulnessActividad;

@Repository
public interface MindfulnessActividadRepository extends JpaRepository<MindfulnessActividad, Integer> {
    List<MindfulnessActividad> findByIdNivel(Integer idNivel);
}