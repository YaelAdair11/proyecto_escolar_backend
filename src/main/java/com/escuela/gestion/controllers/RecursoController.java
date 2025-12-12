package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Recurso;
import com.escuela.gestion.models.Asignacion;
import com.escuela.gestion.repositories.RecursoRepository;
import com.escuela.gestion.repositories.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecursoController {

    @Autowired
    private RecursoRepository recursoRepository;
    @Autowired
    private AsignacionRepository asignacionRepository;

    // GET: Listar
    @GetMapping("/asignaciones/{id}/recursos")
    public List<Recurso> getRecursosPorAsignacion(@PathVariable Long id) {
        return recursoRepository.findByAsignacionId(id);
    }

    // POST: Crear (Con validaciones WOW)
    @PostMapping("/recursos")
    public ResponseEntity<?> crearRecurso(@RequestBody Map<String, Object> payload) {
        try {
            Recurso recurso = new Recurso();
            String tipo = (String) payload.get("tipo");
            String url = (String) payload.get("url");
            String titulo = (String) payload.get("titulo");

            // VALIDACIONES DE BACKEND
            if (titulo == null || titulo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El título es obligatorio."));
            }

            if ("link".equals(tipo)) {
                if (url == null || !url.startsWith("http")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "La URL es inválida (debe empezar con http/https)."));
                }
                recurso.setUrl(url);
            } else if ("file".equals(tipo)) {
                String archivo = (String) payload.get("archivoBase64");
                if (archivo == null || archivo.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Debes adjuntar un archivo."));
                }
                recurso.setArchivoBase64(archivo);
                recurso.setUrl(""); // Sin URL externa
            }

            recurso.setTitulo(titulo);
            recurso.setTipo(tipo);
            recurso.setCreatedAt(LocalDateTime.now());

            Object asignacionIdObj = payload.get("asignacionId");
            if (asignacionIdObj != null) {
                Long asignacionId = Long.valueOf(asignacionIdObj.toString());
                Asignacion asignacion = asignacionRepository.findById(asignacionId)
                    .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
                recurso.setAsignacion(asignacion);
            }

            Recurso guardado = recursoRepository.save(recurso);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    // DELETE: Borrar
    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<?> borrarRecurso(@PathVariable Long id) {
        if (!recursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recursoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Recurso eliminado correctamente"));
    }
}