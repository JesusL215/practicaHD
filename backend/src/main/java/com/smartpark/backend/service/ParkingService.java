package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.*;
import com.smartpark.backend.pattern.decorator.*;
import com.smartpark.backend.pattern.factory.VehiculoFactory;
import com.smartpark.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service // Le dice a Spring que esta clase es un servicio de lógica de negocio
@RequiredArgsConstructor // Lombok crea un constructor con los repositorios automáticamente
public class ParkingService {

    private final VehiculoRepository vehiculoRepository;
    private final TicketRepository ticketRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    @Transactional
    public Ticket registrarEntrada(String placa, String tipoVehiculo, Long slotId) throws Exception {
        // 1. Buscar o crear vehículo
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
                .orElseGet(() -> {
                    Vehiculo nuevo = VehiculoFactory.createVehiculo(tipoVehiculo, placa, "N/A");
                    return vehiculoRepository.save(nuevo);
                });

        // 2. Buscar y ocupar el espacio
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new Exception("El slot no existe."));

        if ("OCUPADO".equals(slot.getEstado())) {
            throw new Exception("El slot ya está ocupado.");
        }
        slot.setEstado("OCUPADO");
        parkingSlotRepository.save(slot);

        // 3. Crear y guardar el Ticket
        Ticket ticket = new Ticket();
        ticket.setVehiculo(vehiculo);
        ticket.setParkingSlot(slot);
        ticket.setHoraEntrada(LocalDateTime.now());
        ticket.setEstado("ACTIVO");

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket registrarSalida(Long ticketId, boolean conLavado) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new Exception("Ticket no encontrado."));

        if ("PAGADO".equals(ticket.getEstado())) {
            throw new Exception("El ticket ya fue pagado.");
        }

        ticket.setHoraSalida(LocalDateTime.now());

        // --- PATRÓN DECORATOR PARA EL COSTO ---
        IParkingCost costoFinal = new BaseParkingCost(ticket);
        if (conLavado) {
            costoFinal = new CarWashDecorator(costoFinal);
        }

        ticket.setCostoTotal(costoFinal.getCosto());
        ticket.setEstado("PAGADO");

        // Liberar el espacio
        ParkingSlot slot = ticket.getParkingSlot();
        slot.setEstado("DISPONIBLE");
        parkingSlotRepository.save(slot);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket registrarSalidaPorPlaca(String placa, boolean conLavado) throws Exception {
        // 1. Buscamos el ticket activo por la placa del vehículo
        Ticket ticket = ticketRepository.findActiveTicketByPlaca(placa)
                .orElseThrow(() -> new Exception("No hay ningún ticket activo para la placa: " + placa));

        // 2. Aplicamos exactamente la misma lógica de cobro y liberación
        ticket.setHoraSalida(LocalDateTime.now());

        IParkingCost costoFinal = new BaseParkingCost(ticket);
        if (conLavado) {
            costoFinal = new CarWashDecorator(costoFinal);
        }

        ticket.setCostoTotal(costoFinal.getCosto());
        ticket.setEstado("PAGADO");

        ParkingSlot slot = ticket.getParkingSlot();
        slot.setEstado("DISPONIBLE");
        parkingSlotRepository.save(slot);

        return ticketRepository.save(ticket);
    }
}