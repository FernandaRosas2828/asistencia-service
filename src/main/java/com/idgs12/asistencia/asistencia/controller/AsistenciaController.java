package com.idgs12.asistencia.asistencia.controller;

import com.idgs12.asistencia.asistencia.dto.RegistroIoTDTO;
import com.idgs12.asistencia.asistencia.dto.AsistenciaResponseDTO;
import com.idgs12.asistencia.asistencia.entity.AsistenciaEntity;
import com.idgs12.asistencia.asistencia.services.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    // ================================
    // ENDPOINT PARA EL IOT
    // ================================
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAsistenciaIoT(@RequestBody RegistroIoTDTO registro) {
        System.out.println("\n📱 ========================================");
        System.out.println("   NUEVO REGISTRO IoT");
        System.out.println("   Matrícula: " + registro.getMatricula());
        System.out.println("   Timestamp: " + registro.getTimestamp());
        System.out.println("========================================\n");

        try {
            if (registro.getMatricula() == null || registro.getMatricula().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearError("Matrícula requerida"));
            }

            if (registro.getTimestamp() == null) {
                registro.setTimestamp(LocalDateTime.now());
            }

            AsistenciaResponseDTO asistencia = asistenciaService.registrarAsistenciaIoT(registro);

            return ResponseEntity.ok(crearRespuestaExitosa(asistencia));

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(crearError(e.getMessage()));
        }
    }

    // ================================
    // PING (Verificar que el servicio está activo)
    // ================================
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Servicio de Asistencias activo");
        return ResponseEntity.ok(response);
    }

    // ================================
    // LISTAR TODAS LAS ASISTENCIAS
    // ================================
    @GetMapping("/all")
    public ResponseEntity<List<AsistenciaEntity>> getAllAsistencias() {
        try {
            List<AsistenciaEntity> asistencias = asistenciaService.getAllAsistencias();
            return ResponseEntity.ok(asistencias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ================================
    // BUSCAR POR MATRÍCULA
    // ================================
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<List<AsistenciaEntity>> getAsistenciasPorMatricula(
            @PathVariable String matricula) {
        try {
            List<AsistenciaEntity> asistencias = asistenciaService.getAsistenciasPorMatricula(matricula);
            return ResponseEntity.ok(asistencias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ================================
    // UTILIDADES
    // ================================
    private Map<String, Object> crearRespuestaExitosa(AsistenciaResponseDTO asistencia) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ Asistencia registrada");
        response.put("tipo", asistencia.getTipoAsistencia().toString());
        response.put("alumno", asistencia.getNombreAlumno());
        response.put("materia", asistencia.getNombreMateria());
        response.put("hora", asistencia.getFecha().toLocalTime());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> crearError(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", mensaje);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // ================================
    // OBTENER ASISTENCIAS CON DETALLE
    // ================================
    @GetMapping("/alumno/{matricula}/detalle")
    public ResponseEntity<List<AsistenciaResponseDTO>> getAsistenciasConDetalle(
            @PathVariable String matricula) {
        try {
            List<AsistenciaResponseDTO> asistencias = asistenciaService.getAsistenciasConDetalle(matricula);
            return ResponseEntity.ok(asistencias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
