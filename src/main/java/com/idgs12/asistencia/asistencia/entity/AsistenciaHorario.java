package com.idgs12.asistencia.asistencia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "asistencia_horario")
@Data
public class AsistenciaHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "asistencia_id", nullable = false)
    private AsistenciaEntity asistencia;

    @Column(name = "horario_id", nullable = false)
    private Integer horarioId;
}
