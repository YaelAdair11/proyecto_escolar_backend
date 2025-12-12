package com.escuela.gestion.controllers;

import com.escuela.gestion.models.*;
import com.escuela.gestion.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController // Le decimos a Spring que esta clase maneja peticiones web (API REST)
@RequestMapping("/api") // Todas las rutas empiezan con /api
@CrossOrigin(origins = "*") // ¡Importante! Deja pasar a React (CORS)
public class AsistenciaController {

    // Inyectamos los repositorios para poder hablar con la Base de Datos
    @Autowired private AsistenciaRepository asistenciaRepo;
    @Autowired private AsignacionRepository asignacionRepo;
    @Autowired private AlumnoRepository alumnoRepo;

    // --- MÉTODO AUXILIAR (Utilería) ---
    // A veces React manda la fecha sucia con hora (ej: "2025-12-11T00:00...").
    // Este método limpia el string para quedarnos solo con "2025-12-11" y que Java no explote.
    private LocalDate parsearFechaSegura(String fechaStr) {
        if (fechaStr.length() > 10) {
            fechaStr = fechaStr.substring(0, 10);
        }
        return LocalDate.parse(fechaStr);
    }

    // --- CARGAR LA LISTA DEL DÍA ---
    // GET: /api/asignaciones/{id}/asistencia?fecha=YYYY-MM-DD
    @GetMapping("/asignaciones/{id}/asistencia")
    public ResponseEntity<?> getAsistencia(@PathVariable Long id, @RequestParam String fecha) {
        try {
            LocalDate localDate = parsearFechaSegura(fecha);
            
            // 1. Buscamos el curso. Si no existe, lanzamos error.
            Asignacion asignacion = asignacionRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            
            // 2. Obtenemos SOLO los alumnos inscritos en este curso (gracias a la relación ManyToMany)
            List<Alumno> alumnosInscritos = asignacion.getAlumnos();

            // 3. Buscamos si ya pasé lista ese día (para recuperar lo que guardé antes)
            List<Asistencia> asistenciasGuardadas = asistenciaRepo.findByAsignacionIdAndFecha(id, localDate);

            // 4. Fusionamos los datos para enviarlos bonitos al frontend
            List<Map<String, Object>> respuesta = new ArrayList<>();

            for (Alumno alumno : alumnosInscritos) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", alumno.getId());
                item.put("nombre", alumno.getNombre());
                item.put("apellido", alumno.getApellido());
                item.put("matricula", alumno.getMatricula());

                // Checamos: ¿Este alumno ya tiene registro hoy?
                Optional<Asistencia> asistenciaExistente = asistenciasGuardadas.stream()
                        .filter(a -> a.getAlumno().getId().equals(alumno.getId()))
                        .findFirst();

                // Si existe, ponemos su estado (ausente/retardo). Si no, por default "presente".
                item.put("status", asistenciaExistente.map(Asistencia::getStatus).orElse("presente"));
                
                respuesta.add(item);
            }
            
            // Ordenamos alfabéticamente por Apellido para que sea fácil pasar lista
            respuesta.sort((a, b) -> ((String) a.get("apellido")).compareTo((String) b.get("apellido")));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            // Si algo falla, avisamos al frontend
            return ResponseEntity.badRequest().body(Map.of("error", "Error al cargar lista: " + e.getMessage()));
        }
    }

    // --- GUARDAR ASISTENCIA ---
    // POST: /api/asistencia
    @PostMapping("/asistencia")
    public ResponseEntity<?> guardarAsistencia(@RequestBody Map<String, Object> payload) {
        try {
            // Leemos los datos que nos manda React
            Long asignacionId = Long.valueOf(payload.get("asignacionId").toString());
            String fechaStr = payload.get("fecha").toString();
            LocalDate fecha = parsearFechaSegura(fechaStr);
            
            // Esta es la lista de alumnos con sus palomitas/taches
            List<Map<String, Object>> listaAlumnos = (List<Map<String, Object>>) payload.get("asistencias");

            Asignacion asignacion = asignacionRepo.findById(asignacionId).orElseThrow();

            // Recorremos alumno por alumno para guardar
            for (Map<String, Object> item : listaAlumnos) {
                Long alumnoId = Long.valueOf(item.get("alumnoId").toString());
                String status = (String) item.get("status");

                Alumno alumno = alumnoRepo.findById(alumnoId).orElseThrow();

                // LÓGICA DE UPSERT (Update or Insert):
                // Buscamos si ya existe registro para NO duplicarlo.
                Asistencia asistencia = asistenciaRepo.findByAsignacionIdAndAlumnoIdAndFecha(asignacionId, alumnoId, fecha)
                        .orElse(new Asistencia()); // Si no existe, creamos uno nuevo vacío.

                // Llenamos los datos (sea nuevo o viejo)
                if (asistencia.getId() == null) {
                    asistencia.setAsignacion(asignacion);
                    asistencia.setAlumno(alumno);
                    asistencia.setFecha(fecha);
                }
                
                asistencia.setStatus(status); // Actualizamos si vino o faltó
                asistenciaRepo.save(asistencia); // Guardamos en la BD
            }

            return ResponseEntity.ok(Map.of("message", "Asistencia guardada correctamente"));
        } catch (Exception e) {
            e.printStackTrace(); // Imprimimos error en consola por si acaso
            return ResponseEntity.badRequest().body(Map.of("error", "Error backend: " + e.getMessage()));
        }
    }

    // --- REPORTE DE HISTORIAL DE ASISTENCIA (LA SÁBANA) ---
    // GET: /api/asignaciones/{id}/reporte-asistencia?inicio=...&fin=...
    @GetMapping("/asignaciones/{id}/reporte-asistencia")
    public ResponseEntity<?> getReporteAsistencia(
            @PathVariable Long id, 
            @RequestParam String inicio, 
            @RequestParam String fin) {
        try {
            // Limpiamos las fechas del rango
            LocalDate fechaInicio = parsearFechaSegura(inicio);
            LocalDate fechaFin = parsearFechaSegura(fin);

            Asignacion asignacion = asignacionRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            
            List<Alumno> alumnos = asignacion.getAlumnos();
            
            // TRUCO DE EFICIENCIA:
            // En lugar de ir a la BD mil veces, vamos UNA sola vez y traemos 
            // todas las asistencias de ese rango de fechas.
            List<Asistencia> registros = asistenciaRepo.findByAsignacionIdAndFechaBetweenOrderByFechaAsc(id, fechaInicio, fechaFin);

            // Ahora armamos la estructura compleja para la tabla tipo Excel
            List<Map<String, Object>> reporte = new ArrayList<>();

            for (Alumno alumno : alumnos) {
                Map<String, Object> fila = new HashMap<>();
                // Datos básicos del alumno
                fila.put("id", alumno.getId());
                fila.put("nombre", alumno.getNombre());
                fila.put("apellido", alumno.getApellido());
                fila.put("matricula", alumno.getMatricula());

                // Aquí vamos a guardar día por día: "2023-12-01": "presente"
                Map<String, String> asistenciaPorDia = new HashMap<>();
                
                // Contadores para el resumen final de la fila
                int totalPresentes = 0;
                int totalAusentes = 0;
                int totalRetardos = 0;

                // Buscamos en la bolsa de registros cuáles son de ESTE alumno
                for (Asistencia a : registros) {
                    if (a.getAlumno().getId().equals(alumno.getId())) {
                        // Guardamos la fecha como llave y el estatus como valor
                        asistenciaPorDia.put(a.getFecha().toString(), a.getStatus());
                        
                        // Sumamos a los contadores
                        if(a.getStatus().equals("presente")) totalPresentes++;
                        else if(a.getStatus().equals("ausente")) totalAusentes++;
                        else if(a.getStatus().equals("tardanza")) totalRetardos++;
                    }
                }
                
                // Agregamos todo a la fila del alumno
                fila.put("asistencias", asistenciaPorDia);
                fila.put("totalPresentes", totalPresentes);
                fila.put("totalAusentes", totalAusentes);
                fila.put("totalRetardos", totalRetardos);

                reporte.add(fila);
            }
            
            // Ordenamos por apellido para que se vea ordenado
            reporte.sort((a, b) -> ((String) a.get("apellido")).compareTo((String) b.get("apellido")));

            return ResponseEntity.ok(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error generando reporte: " + e.getMessage()));
        }
    }
}