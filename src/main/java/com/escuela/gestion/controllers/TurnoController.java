package com.escuela.gestion.controllers;

import com.escuela.gestion.models.Turno;
import com.escuela.gestion.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {
    @Autowired
    private TurnoRepository turnoRepository;

    @GetMapping
    public List<Turno> getAll() {
        return turnoRepository.findAll();
    }

    @PostMapping
    public Turno create(@RequestBody Turno turno) {
        return turnoRepository.save(turno);
    }

    // EDITAR TURNO
    @PutMapping("/{id}")
    public Turno update(@PathVariable Long id, @RequestBody Turno turnoDetails) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setNombreTurno(turnoDetails.getNombreTurno());
                    return turnoRepository.save(turno);
                })
                .orElse(null);
    }

    // ELIMINAR TURNO
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        turnoRepository.deleteById(id);
    }
}