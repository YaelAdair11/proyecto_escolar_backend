package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Alumno;
import com.escuela.gestion.models.Asignacion;
import com.escuela.gestion.models.Calificacion;
import com.escuela.gestion.repositories.AlumnoRepository;
import com.escuela.gestion.repositories.AsignacionRepository;
import com.escuela.gestion.repositories.CalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CalificacionController {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;
    
    @Autowired
    private AlumnoRepository alumnoRepository;

    // GET: Obtener lista de alumnos con su calificación (si la tienen)
    @GetMapping("/asignaciones/{id}/alumnos-con-calificacion")
    public List<Map<String, Object>> obtenerAlumnosConNota(@PathVariable Long id) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        List<Alumno> alumnos = asignacion.getAlumnos();
        
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Alumno alumno : alumnos) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", alumno.getId());
            item.put("nombre", alumno.getNombre());
            item.put("apellido", alumno.getApellido());
            item.put("matricula", alumno.getMatricula());

            // Buscar si ya tiene calificación
            Optional<Calificacion> calif = calificacionRepository.findByAsignacionIdAndAlumnoId(id, alumno.getId());
            
            // Si existe, ponemos el valor. Si no, null.
            item.put("calificacion", calif.map(Calificacion::getValor).orElse(null));
            
            respuesta.add(item);
        }

        return respuesta;
    }

    // POST: Guardar o Actualizar calificación
    @PostMapping("/calificaciones")
    public Map<String, String> guardarCalificacion(@RequestBody Map<String, Object> payload) {
        Long alumnoId = Long.valueOf(payload.get("alumnoId").toString());
        Long asignacionId = Long.valueOf(payload.get("asignacionId").toString());
        
        // Manejo seguro de números (puede venir como entero o decimal)
        Double valor = Double.valueOf(payload.get("calificacion").toString());

        // Buscar si existe para actualizar (Upsert)
        Calificacion calificacion = calificacionRepository.findByAsignacionIdAndAlumnoId(asignacionId, alumnoId)
                .orElse(new Calificacion());

        if (calificacion.getId() == null) {
            // Si es nueva, asignamos las relaciones
            Asignacion asig = asignacionRepository.findById(asignacionId).orElseThrow();
            Alumno alum = alumnoRepository.findById(alumnoId).orElseThrow();
            calificacion.setAsignacion(asig);
            calificacion.setAlumno(alum);
        }

        calificacion.setValor(valor);
        calificacionRepository.save(calificacion);

        return Collections.singletonMap("message", "Calificación guardada correctamente");
    }
}