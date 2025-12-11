package com.escuela.gestion.controllers;

import com.escuela.gestion.models.*;
import com.escuela.gestion.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "*")
public class AsignacionController {

    @Autowired private AsignacionRepository asignacionRepository;
    @Autowired private MaestroRepository maestroRepository;
    @Autowired private MateriaRepository materiaRepository;
    @Autowired private TurnoRepository turnoRepository;

    @GetMapping
    public List<Asignacion> getAll() {
        return asignacionRepository.findAll();
    }

    // Recibimos un mapa con los IDs
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Integer> payload) {
        // Convertimos los IDs (Integer) a Long
        Long maestroId = Long.valueOf(payload.get("maestro_id"));
        Long materiaId = Long.valueOf(payload.get("materia_id"));
        Long turnoId = Long.valueOf(payload.get("turno_id"));

        // Buscamos las entidades
        Optional<Maestro> maestroOpt = maestroRepository.findById(maestroId);
        Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
        Optional<Turno> turnoOpt = turnoRepository.findById(turnoId);

        // Verificamos si alguna entidad no fue encontrada
        if (maestroOpt.isEmpty() || materiaOpt.isEmpty() || turnoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("El maestro, materia o turno especificado no existe.");
        }

        // Si todo existe, creamos la nueva asignación
        Asignacion nueva = new Asignacion();
        nueva.setMaestro(maestroOpt.get());
        nueva.setMateria(materiaOpt.get());
        nueva.setTurno(turnoOpt.get());

        Asignacion savedAsignacion = asignacionRepository.save(nueva);
        return ResponseEntity.ok(savedAsignacion);
    }

    @GetMapping("/{id}/alumnos")
    public ResponseEntity<?> getAlumnosDeAsignacion(@PathVariable Long id) {
        Optional<Asignacion> asignacionOpt = asignacionRepository.findById(id);

        if (asignacionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Asignacion asignacion = asignacionOpt.get();
        return ResponseEntity.ok(asignacion.getAlumnos());
    }
}