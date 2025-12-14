package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Asignacion;
import com.escuela.gestion.models.Recurso;
import com.escuela.gestion.repositories.AsignacionRepository;
import com.escuela.gestion.repositories.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecursoController {

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    // traer los recursos de la materia
    @GetMapping("/asignaciones/{id}/recursos")
    public List<Recurso> getRecursosPorAsignacion(@PathVariable Long id) {
        return recursoRepository.findByAsignacionId(id);
    }

    // subir nuevo material
    @PostMapping("/recursos")
    public ResponseEntity<?> crearRecurso(@RequestBody Map<String, Object> payload) {
        try {
            Recurso recurso = new Recurso();
            return guardarRecurso(recurso, payload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al guardar: " + e.getMessage()));
        }
    }

    // editar un recurso ya existente
    @PutMapping("/recursos/{id}")
    public ResponseEntity<?> editarRecurso(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return recursoRepository.findById(id)
                .map(recurso -> {
                    try {
                        return guardarRecurso(recurso, payload);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Error al actualizar: " + e.getMessage()));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // borrar
    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<?> eliminarRecurso(@PathVariable Long id) {
        if (!recursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recursoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Recurso eliminado"));
    }

    // metodo compartido para guardar porque sirve para create y update
    private ResponseEntity<?> guardarRecurso(Recurso recurso, Map<String, Object> payload) {
        // checar datos obligatorios
        String titulo = (String) payload.get("titulo");
        String tipo = (String) payload.get("type"); // para link o archivo
        
        // conversion manual del id porque el json es engañoso con los tipos
        Object asignacionIdObj = payload.get("asignacionId");
        Long asignacionId = (asignacionIdObj instanceof Number) ? ((Number) asignacionIdObj).longValue() : Long.parseLong(asignacionIdObj.toString());

        if (titulo == null || titulo.trim().isEmpty() || tipo == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan datos obligatorios (titulo, type)"));
        }

        // datos basicos
        recurso.setTitulo(titulo);
        recurso.setTipo(tipo);

        // logica especifica segun el tipo de recurso
        if ("link".equals(tipo)) {
            String url = (String) payload.get("url");
            if (url == null || url.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La URL es obligatoria para recursos tipo enlace"));
            }
            recurso.setUrl(url);
            recurso.setArchivoBase64(""); // si es link no ocupamos guardar archivo
        } else if ("file".equals(tipo)) {
            String base64 = (String) payload.get("archivoBase64");
            // solo cambiamos el archivo si el usuario subio uno nuevo, si no dejamos el anterior
            if (base64 != null && !base64.isEmpty()) {
                recurso.setArchivoBase64(base64);
            } else if (recurso.getId() == null) { 
                // si es nuevo a fuerza necesita archivo
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo es obligatorio"));
            }
            recurso.setUrl(""); // limpiamos url si ahora es archivo
        }

        // si es nuevo le ponemos fecha y lo ligamos a la materia
        if (recurso.getId() == null) {
            recurso.setCreatedAt(LocalDateTime.now());
            Asignacion asignacion = asignacionRepository.findById(asignacionId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            recurso.setAsignacion(asignacion);
        }

        Recurso guardado = recursoRepository.save(recurso);
        return ResponseEntity.ok(guardado);
    }
}