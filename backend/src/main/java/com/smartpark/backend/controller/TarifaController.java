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
    public ResponseEntity<List<Tarifa>> obtenerTodas() {
        return ResponseEntity.ok(tarifaRepository.findAll());
    }

    // Nota el <?> que permite devolver tanto la Tarifa como un String de error
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTarifa(@PathVariable Long id, @RequestBody Tarifa detalles) {
        try {
            Tarifa tarifa = tarifaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

            tarifa.setMonto(detalles.getMonto());
            return ResponseEntity.ok(tarifaRepository.save(tarifa));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}