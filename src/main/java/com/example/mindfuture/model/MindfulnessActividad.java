package com.example.mindfuture.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mindfulness_actividad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MindfulnessActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idActividad;

    @ManyToOne
    @JoinColumn(name = "id_nivel", nullable = false)
    private NivelMindfulness nivel;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "estrellas_recompensa", nullable = false)
    private Integer estrellasRecompensa = 0;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('respiracion', 'relajacion', 'meditacion')")
    private TipoActividad tipo;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_estimada")
    private Integer duracionEstimada; // en minutos

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ActividadUsuario> actividadesUsuarios;

    public enum TipoActividad {
        respiracion, relajacion, meditacion
    }
}