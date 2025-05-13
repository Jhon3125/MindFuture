package com.example.mindfuture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mindfuture.model.NivelMindfulness;

@Repository
public interface NivelMindfulnessRepository extends JpaRepository<NivelMindfulness, Integer> {
    Optional<NivelMindfulness> findByOrden(Integer orden);
}