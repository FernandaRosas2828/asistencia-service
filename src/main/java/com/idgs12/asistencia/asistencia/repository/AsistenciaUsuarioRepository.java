package com.idgs12.asistencia.asistencia.repository;

import com.idgs12.asistencia.asistencia.entity.AsistenciaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaUsuarioRepository extends JpaRepository<AsistenciaUsuario, Integer> {

    List<AsistenciaUsuario> findByAsistencia_Id(Integer asistenciaId);

    List<AsistenciaUsuario> findByUsuarioId(Long usuarioId);

    void deleteByAsistencia_Id(Integer asistenciaId);
}
