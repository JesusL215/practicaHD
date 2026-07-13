package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.model.dto.ReporteDashboardDTO;
import com.smartpark.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service // Esto le dice a Spring Boot que cree y gestione esta clase
public class TicketService {

    private final TicketRepository ticketRepository;

    // Inyectamos el repositorio a través del constructor
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public ReporteDashboardDTO generarDatosDashboard() {
        // Agregamos  a la lista
        List ticketsPagados = ticketRepository.findAll().stream()
                .filter(t -> "PAGADO".equals(t.getEstado()))
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Agregamos  a los Mapas
        Map porDia = ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getHoraSalida().format(formatter),
                        Collectors.summingDouble(Ticket::getCostoTotal)
                ));

        Map porTipo = ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getVehiculo().getTipoVehiculo(),
                        Collectors.summingDouble(Ticket::getCostoTotal)
                ));

        return new ReporteDashboardDTO(porDia, porTipo);
    }
}