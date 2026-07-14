package com.smartpark.backend.repository;

import com.smartpark.backend.model.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    //Buscar el ticket activo de un vehículo usando su placa
    @Query("SELECT t FROM Ticket t WHERE t.vehiculo.placa = :placa AND t.estado = 'ACTIVO'")
    Optional<Ticket> findActiveTicketByPlaca(@Param("placa") String placa);

    @Query("SELECT t.vehiculo.placa FROM Ticket t WHERE t.estado = 'ACTIVO'")
    List<String> findPlacasActivas();
}