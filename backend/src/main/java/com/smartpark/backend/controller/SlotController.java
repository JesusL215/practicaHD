package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.ParkingSlot;
import com.smartpark.backend.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final ParkingSlotService slotService;

    // GET /api/slots -> Lista todos
    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllSlots() {
        return ResponseEntity.ok(slotService.obtenerTodos());
    }

    // POST /api/slots -> Crea uno nuevo
    @PostMapping
    public ResponseEntity<?> crearSlot(@RequestBody ParkingSlot slot) {
        try {
            ParkingSlot nuevoSlot = slotService.crearEspacio(slot);
            return ResponseEntity.ok(nuevoSlot);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/slots/{id} -> Actualiza uno existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSlot(@PathVariable Long id, @RequestBody ParkingSlot slot) {
        try {
            ParkingSlot actualizado = slotService.actualizarEspacio(id, slot);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/slots/{id} -> Elimina uno existente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSlot(@PathVariable Long id) {
        try {
            slotService.eliminarEspacio(id);
            return ResponseEntity.ok("Espacio eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}