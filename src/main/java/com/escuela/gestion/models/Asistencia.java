package com.escuela.gestion.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

// Esta entidad representa el registro diario: si un alumno vino o no en una fecha específica.
@Entity
@Table(name = "asistencias")
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aquí guardamos la fecha de la clase y el estado (Presente, Ausente o Retardo).
    private LocalDate fecha;
    private String status; 

    // Relación con el Alumno: ¿Quién asistió?
    // @JsonIgnoreProperties sirve para evitar errores técnicos al convertir los datos a JSON.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Alumno alumno;

    // Relación con la Asignación: ¿A qué curso o materia pertenece esta asistencia?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Asignacion asignacion;

    // Getters y Setters: Métodos necesarios para poder leer y guardar información en estos campos.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
    public Asignacion getAsignacion() { return asignacion; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
}