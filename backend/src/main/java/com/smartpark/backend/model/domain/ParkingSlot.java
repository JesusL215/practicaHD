package com.smartpark.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true evita espacios duplicados en la base de datos
    @Column(unique = true, nullable = false, length = 10)
    private String numero;

    // Estados permitidos: DISPONIBLE, OCUPADO, RESERVADO, MANTENIMIENTO
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false, length = 20)
    private String tipoVehiculoPermitido;

    @Transient
    private String placaActiva;

    public String getPlacaActiva() {
        return placaActiva;
    }

    public void setPlacaActiva(String placaActiva) {
        this.placaActiva = placaActiva;
    }

}