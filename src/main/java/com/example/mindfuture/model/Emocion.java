package com.example.mindfuture.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emocion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmocion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_registro", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "emocion_detectada", length = 50)
    private String emocionDetectada;

    @Column(name = "nivel_estres")
    private Integer nivelEstres;

    @Column(columnDefinition = "TEXT")
    private String recomendacion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('voz', 'texto', 'biometria')")
    private Fuente fuente;

    public enum Fuente {
        voz, texto, biometria
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = new Date();
    }
}