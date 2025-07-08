package com.example.mindfuture.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import lombok.ToString;

@Entity
@Table(name = "terapeutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"usuario", "sesiones"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idTerapeuta")
public class Terapeuta {
    @Id
    private Integer idTerapeuta;

    @Column(length = 100)
    private String especialidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "certificado_url", length = 255)
    private String certificadoUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id_terapeuta")
    @JsonManagedReference
    private Usuario usuario;

    @OneToMany(mappedBy = "terapeuta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Sesion> sesiones = new HashSet<>();
}
