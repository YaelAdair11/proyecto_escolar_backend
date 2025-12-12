package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Alumno;
import com.escuela.gestion.models.Asignacion;
import com.escuela.gestion.repositories.AlumnoRepository;
import com.escuela.gestion.repositories.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InscripcionController {

    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;

    // GET: Obtener IDs de alumnos ya inscritos en un curso (Para llenar los checkboxes)
    @GetMapping("/inscripciones/{asignacionId}")
    public ResponseEntity<?> obtenerInscritos(@PathVariable Long asignacionId) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        // Devolvemos solo los IDs para el frontend
        List<Long> inscritosIds = asignacion.getAlumnos().stream()
                .map(Alumno::getId)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(inscritosIds);
    }

    // POST: Guardar la lista de alumnos en el curso (Sobreescribe la lista anterior)
    @PostMapping("/inscripciones")
    public ResponseEntity<?> inscribirAlumnos(@RequestBody Map<String, Object> payload) {
        try {
            Long asignacionId = Long.valueOf(payload.get("asignacionId").toString());
            List<Integer> alumnosIdsInt = (List<Integer>) payload.get("alumnoIds");
            
            // Convertir lista de Integer a Long 
            List<Long> alumnosIds = alumnosIdsInt.stream().map(Long::valueOf).collect(Collectors.toList());

            Asignacion asignacion = asignacionRepository.findById(asignacionId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            List<Alumno> alumnosParaInscribir = alumnoRepository.findAllById(alumnosIds);

            // Actualizamos la relación
            asignacion.setAlumnos(alumnosParaInscribir);
            asignacionRepository.save(asignacion);

            return ResponseEntity.ok(Map.of("message", "Alumnos inscritos correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al inscribir: " + e.getMessage()));
        }
    }
}
