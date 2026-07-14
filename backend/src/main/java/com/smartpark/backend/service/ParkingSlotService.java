package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.ParkingSlot;
import com.smartpark.backend.model.domain.Ticket; // <-- Nueva importación
import com.smartpark.backend.repository.ParkingSlotRepository;
import com.smartpark.backend.repository.TicketRepository; // <-- Nueva importación
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSlotService {

    private final ParkingSlotRepository slotRepository;
    private final TicketRepository ticketRepository; // <-- 1. Solo agregamos esto. Lombok hace el resto.

    public List<ParkingSlot> obtenerTodos() {
        // 2. Aquí cruzamos los datos antes de devolver la lista
        List<ParkingSlot> slots = slotRepository.findAll();

        // Buscamos los tickets que están en el estacionamiento ahora mismo
        List<Ticket> ticketsActivos = ticketRepository.findAll().stream()
                .filter(t -> "ACTIVO".equals(t.getEstado()))
                .collect(Collectors.toList());

        // Cruzamos la información
        for (ParkingSlot slot : slots) {
            if ("OCUPADO".equals(slot.getEstado())) {
                ticketsActivos.stream()
                        .filter(t -> t.getParkingSlot() != null && t.getParkingSlot().getId().equals(slot.getId()))
                        .findFirst()
                        .ifPresent(t -> slot.setPlacaActiva(t.getVehiculo().getPlaca()));
            }
        }

        return slots;
    }

    public ParkingSlot crearEspacio(ParkingSlot slot) {
        if (slotRepository.existsByNumero(slot.getNumero())) {
            throw new IllegalArgumentException("Ya existe un espacio con el número " + slot.getNumero());
        }
        return slotRepository.save(slot);
    }

    public ParkingSlot actualizarEspacio(Long id, ParkingSlot detalles) {
        ParkingSlot slotExistente = slotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        // Validar si están intentando cambiarle el número a uno que ya existe en otro ID
        if (!slotExistente.getNumero().equals(detalles.getNumero()) &&
                slotRepository.existsByNumero(detalles.getNumero())) {
            throw new IllegalArgumentException("El número de espacio ya está en uso.");
        }

        slotExistente.setNumero(detalles.getNumero());
        slotExistente.setEstado(detalles.getEstado());
        slotExistente.setTipoVehiculoPermitido(detalles.getTipoVehiculoPermitido());

        return slotRepository.save(slotExistente);
    }

    public void eliminarEspacio(Long id) {
        ParkingSlot slotExistente = slotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        // Regla de Negocio Crítica: Proteger la integridad referencial
        if ("OCUPADO".equals(slotExistente.getEstado())) {
            throw new IllegalStateException("No se puede eliminar un espacio que está actualmente ocupado.");
        }

        slotRepository.delete(slotExistente);
    }
}