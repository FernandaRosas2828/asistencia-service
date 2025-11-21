package com.idgs12.asistencia.asistencia.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String rol;
    private String matricula;
}
