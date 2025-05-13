package com.example.mindfuture.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "actividad_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idActividadUsuario;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario" , nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_actividad", nullable = false)
    private MindfulnessActividad actividad;
    
    @Column(name = "fecha_realizacion", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRealizacion;
    
    @Column(nullable = false)
    private Boolean completado = false;
    
    @PrePersist
    protected void onCreate() {
        fechaRealizacion = new Date();
    }
}