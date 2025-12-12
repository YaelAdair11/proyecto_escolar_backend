package com.escuela.gestion.repositories;

import com.escuela.gestion.models.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// Este repositorio nos permite guardar y buscar notas en la base de datos sin escribir SQL manual.
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    // 1. Búsqueda Individual:
    // Busca la calificación de UN alumno específico en UNA materia.
    // Usamos Optional porque puede que el alumno aún no tenga nota asignada.
    Optional<Calificacion> findByAsignacionIdAndAlumnoId(Long asignacionId, Long alumnoId);

    // 2. Búsqueda Grupal:
    // Recupera la lista de TODAS las calificaciones de un curso.
    // Esto se usa para pintar la tabla completa en la pantalla del profesor.
    List<Calificacion> findByAsignacionId(Long asignacionId);
}