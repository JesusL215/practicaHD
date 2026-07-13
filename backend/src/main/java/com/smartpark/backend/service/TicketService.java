package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.model.dto.ReporteDashboardDTO;
import com.smartpark.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public ReporteDashboardDTO generarDatosDashboard() {

        List<Ticket> ticketsPagados = ticketRepository.findAll().stream()
                .filter(t -> "PAGADO".equals(t.getEstado()))
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, Double> porDia = ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getHoraSalida().format(formatter),
                        Collectors.summingDouble(Ticket::getCostoTotal)
                ));

        Map<String, Double> porTipo = ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getVehiculo().getClass().getSimpleName().toUpperCase(),
                        Collectors.summingDouble(Ticket::getCostoTotal)
                ));

        return new ReporteDashboardDTO(porDia, porTipo);
    }
}