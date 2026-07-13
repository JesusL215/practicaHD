package com.smartpark.backend.repository;

import com.smartpark.backend.model.domain.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    Optional<Tarifa> findByCodigo(String codigo);
}