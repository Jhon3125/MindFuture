package com.example.mindfuture.repository;

import com.example.mindfuture.model.Terapeuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerapeutaRepository extends JpaRepository<Terapeuta, Integer> {
}
