package com.idgs12.asistencia.asistencia.services;

import com.idgs12.asistencia.asistencia.dto.*;
import com.idgs12.asistencia.asistencia.entity.*;
import com.idgs12.asistencia.asistencia.entity.AsistenciaEntity.TipoAsistencia;
import com.idgs12.asistencia.asistencia.repository.*;
import com.idgs12.asistencia.asistencia.FeignClient.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private AsistenciaUsuarioRepository asistenciaUsuarioRepository;

    @Autowired
    private AsistenciaHorarioRepository asistenciaHorarioRepository;

    @Autowired
    private UsuarioFeignClient usuarioFeignClient;

    @Autowired
    private GrupoFeignClient grupoFeignClient;

    @Autowired
    private HorarioFeignClient horarioFeignClient;

    // ================================
    // REGISTRAR ASISTENCIA DESDE IOT
    // ================================
    @Transactional
    public AsistenciaResponseDTO registrarAsistenciaIoT(RegistroIoTDTO registro) {
        System.out.println("\n📱 ========================================");
        System.out.println("   NUEVO REGISTRO IoT");
        System.out.println("   Matrícula: " + registro.getMatricula());
        System.out.println("   Timestamp: " + registro.getTimestamp());
        System.out.println("========================================\n");

        try {
            // 1. Buscar usuario por matrícula
            UsuarioDTO usuario = usuarioFeignClient.getUsuarioByMatricula(registro.getMatricula());
            if (usuario == null) {
                throw new RuntimeException("❌ Usuario no encontrado con matrícula: " + registro.getMatricula());
            }
            System.out.println("✅ Usuario: " + usuario.getNombre() + " " + usuario.getApellidoPaterno());

            // 2. Buscar grupo del usuario
            GrupoDTO grupo = grupoFeignClient.getGrupoPorUsuario(usuario.getId().longValue());
            if (grupo == null) {
                throw new RuntimeException("❌ El usuario no pertenece a ningún grupo");
            }
            System.out.println("✅ Grupo: " + grupo.getNombre());

            // 3. Buscar horario activo en este momento
            HorarioDTO horarioActivo = buscarHorarioActivo(grupo.getId(), registro.getTimestamp());
            if (horarioActivo == null) {
                throw new RuntimeException("❌ No hay clase activa en este momento");
            }
            System.out.println("✅ Horario: " + horarioActivo.getMateria().getNombre() +
                    " (" + horarioActivo.getHoraInicio() + " - " + horarioActivo.getHoraFin() + ")");

            // 4. Determinar tipo de asistencia
            TipoAsistencia tipo = determinarTipoAsistencia(
                    registro.getTimestamp().toLocalTime(),
                    LocalTime.parse(horarioActivo.getHoraInicio()));
            System.out.println("📊 Tipo: " + tipo);

            // 5. Crear asistencia
            AsistenciaEntity asistencia = new AsistenciaEntity();
            asistencia.setMatricula(registro.getMatricula());
            asistencia.setFecha(registro.getTimestamp());
            asistencia.setPresente(tipo != TipoAsistencia.FALTA);
            asistencia.setTipoAsistencia(tipo);

            asistenciaRepository.save(asistencia);

            // 6. Crear relaciones
            crearRelacionUsuario(asistencia, usuario.getId().longValue());
            crearRelacionHorario(asistencia, horarioActivo.getId());

            System.out.println("✅ Asistencia registrada - ID: " + asistencia.getId());

            // 7. Construir respuesta
            return construirRespuesta(asistencia, usuario, horarioActivo);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw new RuntimeException("Error al procesar asistencia: " + e.getMessage());
        }
    }

    // ================================
    // BUSCAR HORARIO ACTIVO
    // ================================
    private HorarioDTO buscarHorarioActivo(Integer grupoId, LocalDateTime timestamp) {
        // Obtener día de la semana en español
        String diaSemana = obtenerDiaSemanaEnEspanol(timestamp.toLocalDate().getDayOfWeek());
        LocalTime horaActual = timestamp.toLocalTime();

        System.out.println("🔍 Buscando horario activo:");
        System.out.println("   - Día: " + diaSemana);
        System.out.println("   - Hora: " + horaActual);

        // Obtener horarios del grupo
        List<HorarioDTO> horarios = horarioFeignClient.getHorariosPorGrupo(grupoId);

        // Buscar horario que coincida con el día y hora
        for (HorarioDTO horario : horarios) {
            if (horario.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                LocalTime horaInicio = LocalTime.parse(horario.getHoraInicio());
                LocalTime horaFin = LocalTime.parse(horario.getHoraFin());

                // Verificar si está dentro del horario (con tolerancia de 30 min después)
                if (horaActual.isAfter(horaInicio.minusMinutes(5)) &&
                        horaActual.isBefore(horaFin.plusMinutes(30))) {
                    return horario;
                }
            }
        }

        return null; // No hay horario activo
    }

    // ================================
    // DETERMINAR TIPO DE ASISTENCIA
    // ================================
    private TipoAsistencia determinarTipoAsistencia(LocalTime horaRegistro, LocalTime horaInicio) {
        long minutosDiferencia = java.time.Duration.between(horaInicio, horaRegistro).toMinutes();

        System.out.println("⏱️ Diferencia: " + minutosDiferencia + " minutos");

        if (minutosDiferencia <= 10) {
            return TipoAsistencia.PRESENTE; // A tiempo (tolerancia 10 min)
        } else if (minutosDiferencia <= 20) {
            return TipoAsistencia.RETRASO; // Retardo (10-20 min)
        } else {
            return TipoAsistencia.FALTA; // Llegó muy tarde (más de 20 min)
        }
    }

    // ================================
    // CREAR RELACIONES
    // ================================
    private void crearRelacionUsuario(AsistenciaEntity asistencia, Long usuarioId) {
        AsistenciaUsuario relacion = new AsistenciaUsuario();
        relacion.setAsistencia(asistencia);
        relacion.setUsuarioId(usuarioId);
        asistenciaUsuarioRepository.save(relacion);
    }

    private void crearRelacionHorario(AsistenciaEntity asistencia, Integer horarioId) {
        AsistenciaHorario relacion = new AsistenciaHorario();
        relacion.setAsistencia(asistencia);
        relacion.setHorarioId(horarioId);
        asistenciaHorarioRepository.save(relacion);
    }

    // ================================
    // CONSTRUIR RESPUESTA
    // ================================
    private AsistenciaResponseDTO construirRespuesta(AsistenciaEntity asistencia,
            UsuarioDTO usuario,
            HorarioDTO horario) {
        AsistenciaResponseDTO response = new AsistenciaResponseDTO();
        response.setId(asistencia.getId());
        response.setMatricula(asistencia.getMatricula());
        response.setFecha(asistencia.getFecha());
        response.setPresente(asistencia.getPresente());
        response.setTipoAsistencia(asistencia.getTipoAsistencia());

        // Info del alumno
        response.setNombreAlumno(usuario.getNombre() + " " + usuario.getApellidoPaterno());

        // Info de la materia/horario
        response.setNombreMateria(horario.getMateria().getNombre());
        response.setDiaSemana(horario.getDiaSemana());
        response.setHoraInicio(horario.getHoraInicio());
        response.setHoraFin(horario.getHoraFin());
        response.setAula(horario.getAula());

        return response;
    }

    // ================================
    // OBTENER DÍA EN ESPAÑOL
    // ================================
    private String obtenerDiaSemanaEnEspanol(DayOfWeek dia) {
        return dia.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    // ================================
    // LISTAR TODAS LAS ASISTENCIAS
    // ================================
    public List<AsistenciaEntity> getAllAsistencias() {
        return asistenciaRepository.findAll();
    }

    // ================================
    // BUSCAR POR MATRÍCULA
    // ================================
    public List<AsistenciaEntity> getAsistenciasPorMatricula(String matricula) {
        return asistenciaRepository.findByMatricula(matricula);
    }
    // ================================
    // HABILITAR ASISTENCIA -- Maria Fernanda Rosas Briones -- IDGS12
    // ================================
    @Transactional
    public boolean habilitarAsistencia(Integer id) {
        AsistenciaEntity asistencia = asistenciaRepository.findById(id).orElse(null);

        if (asistencia == null) {
            return false;
        }

        asistencia.setPresente(true);
        asistenciaRepository.save(asistencia);

        return true;
    }

    // ================================
    // OBTENER ASISTENCIAS CON DETALLE DE MATERIA
    // ================================
    public List<AsistenciaResponseDTO> getAsistenciasConDetalle(String matricula) {
        // 1. Obtener asistencias del alumno
        List<AsistenciaEntity> asistencias = asistenciaRepository.findByMatricula(matricula);

        if (asistencias.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Construir respuestas con información completa
        return asistencias.stream().map(asistencia -> {
            AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
            dto.setId(asistencia.getId());
            dto.setMatricula(asistencia.getMatricula());
            dto.setFecha(asistencia.getFecha());
            dto.setPresente(asistencia.getPresente());
            dto.setTipoAsistencia(asistencia.getTipoAsistencia());

            try {
                // 3. Buscar relación con horario
                List<AsistenciaHorario> relaciones = asistenciaHorarioRepository
                        .findByAsistencia_Id(asistencia.getId());

                if (!relaciones.isEmpty()) {
                    Integer horarioId = relaciones.get(0).getHorarioId();

                    // 4. Obtener información del horario
                    HorarioDTO horario = horarioFeignClient.getHorarioPorId(horarioId);

                    if (horario != null) {
                        dto.setNombreMateria(horario.getMateria().getNombre());
                        dto.setDiaSemana(horario.getDiaSemana());
                        dto.setHoraInicio(horario.getHoraInicio());
                        dto.setHoraFin(horario.getHoraFin());
                        dto.setAula(horario.getAula());
                    }
                }

                List<AsistenciaUsuario> usuarioRelaciones = asistenciaUsuarioRepository
                        .findByAsistencia_Id(asistencia.getId());

                if (!usuarioRelaciones.isEmpty()) {
                    Long usuarioId = usuarioRelaciones.get(0).getUsuarioId();
                    UsuarioDTO usuario = usuarioFeignClient.getUsuarioById(usuarioId);

                    if (usuario != null) {
                        dto.setNombreAlumno(usuario.getNombre() + " " + usuario.getApellidoPaterno());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error al obtener detalle: " + e.getMessage());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
