package com.example.mindfuture.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_progreso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProgreso;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_nivel", referencedColumnName = "id_nivel", nullable = false)
    private NivelMindfulness nivel;

    @Column(name = "estrellas_acumuladas", nullable = false)
    private Integer estrellasAcumuladas = 0;

    @Column(nullable = false)
    private Boolean completado = false;

    @Column(name = "fecha_completado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompletado;
}