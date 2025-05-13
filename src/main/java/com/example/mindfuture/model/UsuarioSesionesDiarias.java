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
@Table(name = "usuario_sesiones_diarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSesionesDiarias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSesionDiaria;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    @Column(name = "sesiones_completadas", nullable = false)
    private Integer sesionesCompletadas = 0;
}