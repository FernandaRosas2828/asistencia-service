package com.idgs12.asistencia.asistencia.repository;

import com.idgs12.asistencia.asistencia.entity.AsistenciaHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaHorarioRepository extends JpaRepository<AsistenciaHorario, Integer> {

    List<AsistenciaHorario> findByAsistencia_Id(Integer asistenciaId);

    List<AsistenciaHorario> findByHorarioId(Integer horarioId);

    void deleteByAsistencia_Id(Integer asistenciaId);
}
