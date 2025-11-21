package com.idgs12.asistencia.asistencia.repository;

import com.idgs12.asistencia.asistencia.entity.AsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<AsistenciaEntity, Integer> {

    List<AsistenciaEntity> findByMatricula(String matricula);

    List<AsistenciaEntity> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
