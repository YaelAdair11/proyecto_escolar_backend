package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Maestro;
import com.escuela.gestion.repositories.MaestroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maestros")
@CrossOrigin(origins = "*")
public class MaestroController {
    @Autowired
    private MaestroRepository maestroRepository;

    @GetMapping
    public List<Maestro> getAll() {
        return maestroRepository.findAll();
    }

    @PostMapping
    public Maestro create(@RequestBody Maestro maestro) {
        return maestroRepository.save(maestro);
    }

    // EDITAR MAESTRO
    @PutMapping("/{id}")
    public Maestro update(@PathVariable Long id, @RequestBody Maestro maestroDetails) {
        return maestroRepository.findById(id)
                .map(maestro -> {
                    maestro.setNombre(maestroDetails.getNombre());
                    maestro.setApellido(maestroDetails.getApellido());
                    maestro.setEmail(maestroDetails.getEmail());
                    maestro.setTelefono(maestroDetails.getTelefono());
                    // Solo actualizamos la contraseña si viene una nueva (no está vacía)
                    if (maestroDetails.getPassword() != null && !maestroDetails.getPassword().isEmpty()) {
                        maestro.setPassword(maestroDetails.getPassword());
                    }
                    return maestroRepository.save(maestro);
                })
                .orElse(null);
    }

    // ELIMINAR MAESTRO
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maestroRepository.deleteById(id);
    }
}