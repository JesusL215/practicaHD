package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ParkingService parkingService;

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(
            @RequestParam String placa,
            @RequestParam String tipoVehiculo,
            @RequestParam Long slotId) {
        try {
            Ticket ticket = parkingService.registrarEntrada(placa, tipoVehiculo, slotId);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/salida/{ticketId}")
    public ResponseEntity<?> registrarSalida(
            @PathVariable Long ticketId,
            @RequestParam boolean conLavado) {
        try {
            Ticket ticket = parkingService.registrarSalida(ticketId, conLavado);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/salida/placa/{placa}")
    public ResponseEntity<?> registrarSalidaPorPlaca(
            @PathVariable String placa,
            @RequestParam boolean conLavado) {
        try {
            Ticket ticket = parkingService.registrarSalidaPorPlaca(placa, conLavado);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}