package com.example.mindfuture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.NivelMindfulness;

@Repository
public interface NivelMindfulnessRepository extends JpaRepository<NivelMindfulness, Integer> {
    NivelMindfulness findByOrden(Integer orden);
}