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

    // GET: Obtener recursos de un curso
    // Se ajusta a tu repositorio usando 'findByAsignacionId'
    @GetMapping("/asignaciones/{id}/recursos")
    public List<Recurso> getRecursosPorAsignacion(@PathVariable Long id) {
        return recursoRepository.findByAsignacionId(id);
    }

    // POST: Crear nuevo recurso
    @PostMapping("/recursos")
    public ResponseEntity<?> crearRecurso(@RequestBody Map<String, Object> payload) {
        try {
            Recurso recurso = new Recurso();
            return guardarRecurso(recurso, payload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al guardar: " + e.getMessage()));
        }
    }

    // PUT: Editar recurso existente
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

    // DELETE: Eliminar recurso
    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<?> eliminarRecurso(@PathVariable Long id) {
        if (!recursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recursoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Recurso eliminado"));
    }

    // --- MÉTODO AUXILIAR PARA GUARDAR (Lógica común para Crear y Editar) ---
    private ResponseEntity<?> guardarRecurso(Recurso recurso, Map<String, Object> payload) {
        // 1. Validar datos obligatorios
        String titulo = (String) payload.get("titulo");
        String tipo = (String) payload.get("type"); // 'link' o 'file'
        
        // Convertimos de forma segura el ID (puede venir como Integer o Long)
        Object asignacionIdObj = payload.get("asignacionId");
        Long asignacionId = (asignacionIdObj instanceof Number) ? ((Number) asignacionIdObj).longValue() : Long.parseLong(asignacionIdObj.toString());

        if (titulo == null || titulo.trim().isEmpty() || tipo == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan datos obligatorios (titulo, type)"));
        }

        // 2. Asignar datos básicos
        recurso.setTitulo(titulo);
        recurso.setTipo(tipo);

        // 3. Lógica para diferenciar Link vs Archivo
        if ("link".equals(tipo)) {
            String url = (String) payload.get("url");
            if (url == null || url.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La URL es obligatoria para recursos tipo enlace"));
            }
            recurso.setUrl(url);
            recurso.setArchivoBase64(null); // Limpiamos archivo si cambiaron a tipo link
        } else if ("file".equals(tipo)) {
            String base64 = (String) payload.get("archivoBase64");
            // Solo actualizamos el archivo si viene uno nuevo. 
            // Si es edición y no viene archivo, mantenemos el que ya estaba (si existe).
            if (base64 != null && !base64.isEmpty()) {
                recurso.setArchivoBase64(base64);
            } else if (recurso.getId() == null) { 
                // Si es nuevo registro y no hay archivo
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo es obligatorio"));
            }
            recurso.setUrl(null); // Limpiamos URL si cambiaron a tipo file
        }

        // 4. Si es nuevo, asignamos fecha y relación con Asignación
        if (recurso.getId() == null) {
            recurso.setCreatedAt(LocalDateTime.now());
            Asignacion asignacion = asignacionRepository.findById(asignacionId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            recurso.setAsignacion(asignacion);
        }

        // 5. Guardar en BD
        Recurso guardado = recursoRepository.save(recurso);
        return ResponseEntity.ok(guardado);
    }
}