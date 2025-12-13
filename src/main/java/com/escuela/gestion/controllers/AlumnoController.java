package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Alumno;
import com.escuela.gestion.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*") // Permite que React se conecte sin problemas
public class AlumnoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    // GET: Obtener todos los alumnos
    @GetMapping
    public List<Alumno> getAllAlumnos() {
        return alumnoRepository.findAll();
    }

    // POST: Guardar un nuevo alumno
    @PostMapping
    public Alumno createAlumno(@RequestBody Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    // --- NUEVOS MÉTODOS PARA COMPLETAR EL CRUD ---

    // PUT: Actualizar un alumno existente
    @PutMapping("/{id}")
    public Alumno updateAlumno(@PathVariable Long id, @RequestBody Alumno alumnoDetails) {
        return alumnoRepository.findById(id)
                .map(alumno -> {
                    // Actualizamos los campos con la información que viene del frontend
                    alumno.setMatricula(alumnoDetails.getMatricula());
                    alumno.setNombre(alumnoDetails.getNombre());
                    alumno.setApellido(alumnoDetails.getApellido());
                    alumno.setDireccion(alumnoDetails.getDireccion());
                    alumno.setFechaNacimiento(alumnoDetails.getFechaNacimiento());
                    
                    return alumnoRepository.save(alumno); // Guardamos los cambios
                })
                .orElse(null); // Retorna null si no encuentra el ID (idealmente se maneja con excepción)
    }

    // DELETE: Eliminar un alumno
    @DeleteMapping("/{id}")
    public void deleteAlumno(@PathVariable Long id) {
        alumnoRepository.deleteById(id);
    }
}