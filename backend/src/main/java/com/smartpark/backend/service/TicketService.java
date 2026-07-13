package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.Ticket;
import com.smartpark.backend.model.dto.MovimientoDTO; // <-- Importación añadida
import com.smartpark.backend.model.dto.ReporteDashboardDTO;
import com.smartpark.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public ReporteDashboardDTO generarDatosDashboard(String fechaFiltro) {

        // CORRECCIÓN 1: Aquí debe decir exactamente List<Ticket>
        List<Ticket> todosLosTickets = ticketRepository.findAll();

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

        ReporteDashboardDTO dto = new ReporteDashboardDTO();

        // 1. Calcular KPIs
        int ingresadosHoy = 0;
        int estacionados = 0;
        double ingresosHoy = 0.0;
        double ingresosMes = 0.0;

        for (Ticket t : todosLosTickets) {
            if (t.getHoraEntrada().toLocalDate().isEqual(hoy)) ingresadosHoy++;
            if ("ACTIVO".equals(t.getEstado())) estacionados++;

            if ("PAGADO".equals(t.getEstado()) && t.getHoraSalida() != null) {
                if (t.getHoraSalida().toLocalDate().isEqual(hoy)) ingresosHoy += t.getCostoTotal();
                if (t.getHoraSalida().toLocalDate().getMonth() == hoy.getMonth()) ingresosMes += t.getCostoTotal();
            }
        }

        dto.setVehiculosIngresadosHoy(ingresadosHoy);
        dto.setVehiculosEstacionados(estacionados);
        dto.setEspaciosLibres(50 - estacionados);
        dto.setIngresosHoy(ingresosHoy);
        dto.setIngresosMes(ingresosMes);

        // 2. Gráficos (Filtramos solo los pagados para ingresos)
        // CORRECCIÓN 2: Aquí debe decir exactamente List<Ticket>
        List<Ticket> ticketsPagados = todosLosTickets.stream()
                .filter(t -> "PAGADO".equals(t.getEstado()))
                .collect(Collectors.toList());

        dto.setIngresosPorDia(ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getHoraSalida().format(formatterFecha),
                        Collectors.summingDouble(Ticket::getCostoTotal))));

        dto.setIngresosPorTipoVehiculo(ticketsPagados.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getVehiculo().getClass().getSimpleName().toUpperCase(),
                        Collectors.summingDouble(Ticket::getCostoTotal))));

        dto.setHorasPico(todosLosTickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getHoraEntrada().getHour() + ":00",
                        Collectors.summingInt(e -> 1))));

        // 3. Tabla de Movimientos Recientes (Últimos 15)
        // CORRECCIÓN 3: Aquí debe decir exactamente List<MovimientoDTO>
        List<MovimientoDTO> movimientos = todosLosTickets.stream()
                .sorted(Comparator.comparing(Ticket::getHoraEntrada).reversed())
                .limit(15)
                .map(t -> new MovimientoDTO(
                        t.getHoraEntrada().format(formatterFecha),
                        t.getHoraEntrada().format(formatterHora),
                        t.getVehiculo().getPlaca(),
                        t.getVehiculo().getClass().getSimpleName().toUpperCase(),
                        t.getEstado(),
                        t.getCostoTotal() != null ? t.getCostoTotal() : 0.0
                )).collect(Collectors.toList());

        dto.setMovimientosRecientes(movimientos);

        return dto;
    }
}