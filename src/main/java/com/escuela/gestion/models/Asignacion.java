package com.escuela.gestion.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

// Esta clase es muy importante: representa la "Carga Académica".
// Une al maestro, la materia y el turno en un solo registro en la base de datos.
@Data
@Entity
@Table(name = "asignaciones")
public class Asignacion {
    
    // Identificador único de cada clase o curso creado.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aquí conectamos con la tabla de Maestros (Un curso tiene un maestro).
    @ManyToOne
    @JoinColumn(name = "maestro_id", nullable = false)
    private Maestro maestro;

    // Aquí indicamos qué Materia se va a impartir en este curso.
    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    // Aquí definimos el horario o Turno de la clase.
    @ManyToOne
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    // Esta parte es clave: Crea una tabla intermedia para guardar la lista
    // de todos los alumnos que están inscritos en este curso específico.
    @ManyToMany
    @JoinTable(
        name = "asignacion_alumnos",
        joinColumns = @JoinColumn(name = "asignacion_id"),
        inverseJoinColumns = @JoinColumn(name = "alumno_id")
    )
    private List<Alumno> alumnos;
}