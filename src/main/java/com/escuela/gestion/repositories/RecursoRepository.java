package com.escuela.gestion.repositories;

import com.escuela.gestion.models.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Este repositorio gestiona la "Biblioteca Virtual" en la base de datos.
// Nos permite guardar, borrar y leer los recursos (PDFs y Links) sin escribir SQL manualmente.
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // Este método es el filtro principal de la biblioteca.
    // Sirve para traer SOLO los materiales de una materia específica (por su ID),
    // asegurando que no se mezclen los archivos de Matemáticas con los de Historia.
    List<Recurso> findByAsignacionId(Long asignacionId);
}