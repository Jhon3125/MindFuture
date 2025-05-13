package com.example.mindfuture.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "terapeutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Terapeuta {
    @Id
    private Integer idTerapeuta;

    @Column(length = 100)
    private String especialidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "certificado_url", length = 255)
    private String certificadoUrl;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_terapeuta")
    private Usuario usuario;

    @OneToMany(mappedBy = "terapeuta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sesion> sesiones = new HashSet<>();
}