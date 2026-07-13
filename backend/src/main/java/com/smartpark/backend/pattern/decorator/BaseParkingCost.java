package com.smartpark.backend.pattern.decorator;

import com.smartpark.backend.model.domain.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseParkingCost implements IParkingCost {
    private final Ticket ticket;
    private final double tarifaPorHora;

    // Ahora exigimos la tarifa en el constructor
    public BaseParkingCost(Ticket ticket, double tarifaPorHora) {
        this.ticket = ticket;
        this.tarifaPorHora = tarifaPorHora;
    }

    @Override
    public double getCosto() {
        LocalDateTime entrada = ticket.getHoraEntrada();
        LocalDateTime salida = ticket.getHoraSalida() != null ? ticket.getHoraSalida() : LocalDateTime.now();

        long horas = Duration.between(entrada, salida).toHours();
        if (horas == 0) horas = 1; // Cobramos mínimo 1 hora

        return horas * tarifaPorHora; // Usamos la tarifa dinámica
    }

    @Override
    public String getDescripcion() {
        return "Servicio de Estacionamiento Base";
    }
}