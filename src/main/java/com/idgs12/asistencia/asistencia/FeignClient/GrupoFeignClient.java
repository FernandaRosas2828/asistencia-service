package com.idgs12.asistencia.asistencia.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.idgs12.asistencia.asistencia.dto.GrupoDTO;

@FeignClient(name = "grupos", url = "http://grupos:8086")
public interface GrupoFeignClient {

    @GetMapping("/grupos/usuario/{usuarioId}")
    GrupoDTO getGrupoPorUsuario(@PathVariable("usuarioId") Long usuarioId);
}
