package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistroIoTDTO {
    private String matricula;
    private LocalDateTime timestamp;
}
