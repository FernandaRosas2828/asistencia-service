package com.idgs12.asistencia.asistencia.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.idgs12.asistencia.asistencia.dto.UsuarioDTO;

@FeignClient(name = "usuarios", url = "http://usuarios:8084")
public interface UsuarioFeignClient {

    @GetMapping("/usuarios/matricula/{matricula}")
    UsuarioDTO getUsuarioByMatricula(@PathVariable("matricula") String matricula);

    @GetMapping("/usuarios/{id}")
    UsuarioDTO getUsuarioById(@PathVariable("id") Long id);
}
