package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.Tarifa;
import com.smartpark.backend.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaRepository tarifaRepository;

    @GetMapping
    public ResponseEntity> obtenerTarifas() {
        return ResponseEntity.ok(tarifaRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity actualizarTarifa(@PathVariable Long id, @RequestBody Tarifa detalles) {
        return tarifaRepository.findById(id).map(tarifa -> {
            tarifa.setMonto(detalles.getMonto()); // Solo permitimos cambiar el precio
            return ResponseEntity.ok(tarifaRepository.save(tarifa));
        }).orElse(ResponseEntity.badRequest().body("Tarifa no encontrada"));
    }
}