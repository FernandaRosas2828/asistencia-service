package com.idgs12.asistencia.asistencia.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "asistencia")
@Data
public class AsistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean presente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asistencia", nullable = false)
    private TipoAsistencia tipoAsistencia = TipoAsistencia.PRESENTE;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public enum TipoAsistencia {
        PRESENTE,
        RETRASO,
        FALTA
    }
}
