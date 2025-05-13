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
@Table(name = "usuario_logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuarioLogro;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario" , nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_logro", nullable = false)
    private Logro logro;

    @Column(name = "fecha_desbloqueo", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDesbloqueo;

    @PrePersist
    protected void onCreate() {
        fechaDesbloqueo = new Date();
    }
}