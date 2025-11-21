package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;

@Data
public class HorarioDTO {
    private Integer id;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String aula;
    private Boolean activo;
    private MateriaDTO materia;
}