package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Materia;
import com.escuela.gestion.repositories.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "*")
public class MateriaController {
    @Autowired
    private MateriaRepository materiaRepository;

    @GetMapping
    public List<Materia> getAll() {
        return materiaRepository.findAll();
    }

    @PostMapping
    public Materia create(@RequestBody Materia materia) {
        return materiaRepository.save(materia);
    }

    // EDITAR MATERIA
    @PutMapping("/{id}")
    public Materia update(@PathVariable Long id, @RequestBody Materia materiaDetails) {
        return materiaRepository.findById(id)
                .map(materia -> {
                    materia.setNombreMateria(materiaDetails.getNombreMateria());
                    materia.setClaveMateria(materiaDetails.getClaveMateria());
                    materia.setDescripcion(materiaDetails.getDescripcion());
                    return materiaRepository.save(materia);
                })
                .orElse(null);
    }

    // ELIMINAR MATERIA
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        materiaRepository.deleteById(id);
    }
}