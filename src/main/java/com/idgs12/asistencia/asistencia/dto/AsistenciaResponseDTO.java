package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;
import com.idgs12.asistencia.asistencia.entity.AsistenciaEntity.TipoAsistencia;
import java.time.LocalDateTime;

@Data
public class AsistenciaResponseDTO {
    private Integer id;
    private String matricula;
    private LocalDateTime fecha;
    private Boolean presente;
    private TipoAsistencia tipoAsistencia;

    private String nombreAlumno;
    private String nombreMateria;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String aula;
}
