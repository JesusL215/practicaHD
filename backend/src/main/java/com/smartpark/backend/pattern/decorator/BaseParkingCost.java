package com.smartpark.backend.pattern.decorator;

import com.smartpark.backend.model.domain.Auto;
import com.smartpark.backend.model.domain.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseParkingCost implements IParkingCost {

    private final Ticket ticket;

    public BaseParkingCost(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public double getCosto() {
        LocalDateTime fin = ticket.getHoraSalida() != null ? ticket.getHoraSalida() : LocalDateTime.now();
        long horas = Duration.between(ticket.getHoraEntrada(), fin).toHours();
        if (horas == 0) horas = 1; // Cobro mínimo de 1 hora

        // Si es Auto cobra 5.0, si es Moto (o cualquier otro) cobra 3.0
        double tarifa = (ticket.getVehiculo() instanceof Auto) ? 5.0 : 3.0;

        return horas * tarifa;
    }

    @Override
    public String getDescripcion() {
        return "Costo base de estacionamiento";
    }
}