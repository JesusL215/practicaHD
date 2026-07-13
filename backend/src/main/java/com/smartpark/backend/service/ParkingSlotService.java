package com.smartpark.backend.service;

import com.smartpark.backend.model.domain.ParkingSlot;
import com.smartpark.backend.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSlotService {

    private final ParkingSlotRepository slotRepository;

    public List<ParkingSlot> obtenerTodos() {
        return slotRepository.findAll();
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