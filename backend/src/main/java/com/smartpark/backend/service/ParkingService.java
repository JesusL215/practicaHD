package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.*;
import com.smartpark.backend.pattern.decorator.*;
import com.smartpark.backend.pattern.factory.VehiculoFactory;
import com.smartpark.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final VehiculoRepository vehiculoRepository;
    private final TicketRepository ticketRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final TarifaRepository tarifaRepository; // ¡NUEVO: Inyectamos el repo de tarifas!

    @Transactional
    public Ticket registrarEntrada(String placa, String tipoVehiculo, Long slotId) throws Exception {
        // ... (Este metodo se queda exactamente igual que antes) ...
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
                .orElseGet(() -> {
                    Vehiculo nuevo = VehiculoFactory.createVehiculo(tipoVehiculo, placa, "N/A");
                    return vehiculoRepository.save(nuevo);
                });

        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new Exception("El slot no existe."));

        if ("OCUPADO".equals(slot.getEstado())) {
            throw new Exception("El slot ya está ocupado.");
        }
        slot.setEstado("OCUPADO");
        parkingSlotRepository.save(slot);

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
        return procesarSalida(ticket, conLavado);
    }

    @Transactional
    public Ticket registrarSalidaPorPlaca(String placa, boolean conLavado) throws Exception {
        Ticket ticket = ticketRepository.findActiveTicketByPlaca(placa)
                .orElseThrow(() -> new Exception("No hay ningún ticket activo para la placa: " + placa));
        return procesarSalida(ticket, conLavado);
    }

    private Ticket procesarSalida(Ticket ticket, boolean conLavado) throws Exception {
        if ("PAGADO".equals(ticket.getEstado())) {
            throw new Exception("El ticket ya fue pagado.");
        }

        ticket.setHoraSalida(LocalDateTime.now());

        // 1. Buscamos la tarifa según el tipo de vehículo
        String tipoVehiculo = ticket.getVehiculo().getClass().getSimpleName().toUpperCase();
        String codigoTarifa = "AUTO".equalsIgnoreCase(tipoVehiculo) ? "TARIFA_AUTO" : "TARIFA_MOTO";

        Tarifa tarifaBase = tarifaRepository.findByCodigo(codigoTarifa)
                .orElseThrow(() -> new Exception("Tarifa base no configurada para: " + tipoVehiculo));

        // 2. Pasamos la tarifa de la BD al Decorator
        IParkingCost costoFinal = new BaseParkingCost(ticket, tarifaBase.getMonto());

        // 3. Aplicamos el Decorator de Lavado con su tarifa de BD
        if (conLavado) {
            Tarifa tarifaLavado = tarifaRepository.findByCodigo("SERVICIO_LAVADO")
                    .orElseThrow(() -> new Exception("Tarifa de lavado no configurada."));
            costoFinal = new CarWashDecorator(costoFinal, tarifaLavado.getMonto());
        }

        ticket.setCostoTotal(costoFinal.getCosto());
        ticket.setEstado("PAGADO");

        ParkingSlot slot = ticket.getParkingSlot();
        slot.setEstado("DISPONIBLE");
        parkingSlotRepository.save(slot);

        return ticketRepository.save(ticket);
    }
}