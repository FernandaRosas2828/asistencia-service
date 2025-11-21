package com.idgs12.asistencia.asistencia.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.idgs12.asistencia.asistencia.dto.HorarioDTO;

import java.util.List;

@FeignClient(name = "horarios", url = "http://horario:8087")
public interface HorarioFeignClient {

    @GetMapping("/horarios/grupo/{grupoId}")
    List<HorarioDTO> getHorariosPorGrupo(@PathVariable("grupoId") Integer grupoId);

    // ✅ AGREGAR ESTE MÉTODO
    @GetMapping("/horarios/{id}")
    HorarioDTO getHorarioPorId(@PathVariable("id") Integer id);
}
