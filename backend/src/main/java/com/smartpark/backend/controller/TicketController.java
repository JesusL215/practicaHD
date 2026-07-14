package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.service.ParkingService;
import com.smartpark.backend.service.PdfReportService;
import com.smartpark.backend.service.TicketService;
import com.smartpark.backend.repository.TicketRepository;
import com.smartpark.backend.model.dto.ReporteDashboardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    @Autowired
    private TicketService ticketService;
    private final ParkingService parkingService;
    private final TicketRepository ticketRepository;
    private final PdfReportService pdfReportService;

    @GetMapping("/{id}/recibo")
    public ResponseEntity<byte[]> descargarRecibo(@PathVariable Long id) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));

            if (!"PAGADO".equals(ticket.getEstado())) {
                return ResponseEntity.badRequest().body(null);
            }

            byte[] pdfBytes = pdfReportService.generarReciboPdf(ticket);

            HttpHeaders headers = new HttpHeaders();
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

    @GetMapping("/reportes/dashboard")
    public ResponseEntity<ReporteDashboardDTO> obtenerDatosDashboard(
            @RequestParam(required = false) String fechaFiltro) {
        return ResponseEntity.ok(ticketService.generarDatosDashboard(fechaFiltro));
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> obtenerTodosLosTickets() {
        // Usamos el servicio para devolver todos los tickets de la base de datos
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }
}