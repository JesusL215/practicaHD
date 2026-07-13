package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartpark.backend.service.PdfReportService;
import com.smartpark.backend.repository.TicketRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ParkingService parkingService;
    private final TicketRepository ticketRepository;
    private final PdfReportService pdfReportService;

    @GetMapping("/{id}/recibo")
    public ResponseEntity descargarRecibo(@PathVariable Long id) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));

            // Si no está pagado, no deberíamos emitir el recibo final
            if (!"PAGADO".equals(ticket.getEstado())) {
                return ResponseEntity.badRequest().body(null);
            }

            byte[] pdfBytes = pdfReportService.generarReciboPdf(ticket);

            HttpHeaders headers = new HttpHeaders();
            // Le decimos al navegador/cliente que esto es un archivo descargable
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Recibo-" + ticket.getVehiculo().getPlaca() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

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

    @GetMapping("/activos/placas")
    public ResponseEntity<List<String>> obtenerPlacasActivas() {
        return ResponseEntity.ok(ticketRepository.findPlacasActivas());
    }
}