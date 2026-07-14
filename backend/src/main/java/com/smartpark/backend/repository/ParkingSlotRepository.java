package com.smartpark.backend.repository;

import com.smartpark.backend.model.domain.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    Optional<ParkingSlot> findByNumero(String numero);
    boolean existsByNumero(String numero);
}