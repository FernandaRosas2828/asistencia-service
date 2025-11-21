package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;

@Data
public class GrupoDTO {
    private Integer id;
    private String nombre;
    private String cuatrimestre;
    private Boolean estado;
}