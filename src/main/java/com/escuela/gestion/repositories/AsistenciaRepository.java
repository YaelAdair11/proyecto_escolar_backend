package com.escuela.gestion.repositories;

import com.escuela.gestion.models.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

// Este archivo es el "puente" con la Base de Datos.
// Al extender de JpaRepository, Spring nos regala la magia de guardar y leer sin escribir SQL manual.
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // 1. MODO DIARIO:
    // Nos trae la lista de todo el salón para una fecha específica.
    // Se usa cuando el profe entra a la pantalla de "Pasar Lista".
    List<Asistencia> findByAsignacionIdAndFecha(Long asignacionId, LocalDate fecha);

    // 2. VALIDACIÓN (Evitar Duplicados):
    // Busca si un alumno en concreto ya tiene falta o asistencia ese día.
    // Es vital para saber si debemos "Crear un registro nuevo" o "Actualizar el que ya existe".
    Optional<Asistencia> findByAsignacionIdAndAlumnoIdAndFecha(Long asignacionId, Long alumnoId, LocalDate fecha);
    
    // 3. MODO REPORTE (La Sábana):
    // Este método es "inteligente": busca las asistencias en un RANGO de fechas (Between).
    // Además, el 'OrderByFechaAsc' asegura que los datos salgan ordenados cronológicamente (Lunes, Martes...), ideal para generar la tabla.
    List<Asistencia> findByAsignacionIdAndFechaBetweenOrderByFechaAsc(Long asignacionId, LocalDate inicio, LocalDate fin);
}