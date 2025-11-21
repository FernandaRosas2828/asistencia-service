package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AsistenciaDTO {

    private int id;
    private String matricula;
    private LocalDateTime fecha;
    private boolean presente;
}
