package com.smartpark.backend.pattern.factory;

import com.smartpark.backend.model.domain.Auto;
import com.smartpark.backend.model.domain.Moto;
import com.smartpark.backend.model.domain.Vehiculo;

public class VehiculoFactory {

    public static Vehiculo createVehiculo(String tipo, String placa, String propietario) {
        Vehiculo vehiculo;

        if ("AUTO".equalsIgnoreCase(tipo)) {
            vehiculo = new Auto();
        } else if ("MOTO".equalsIgnoreCase(tipo)) {
            vehiculo = new Moto();
        } else {
            throw new IllegalArgumentException("Tipo de vehículo no soportado: " + tipo);
        }

        // Asignamos los datos usando los Setters creados por Lombok
        vehiculo.setPlaca(placa);
        vehiculo.setPropietario(propietario);

        return vehiculo;
    }
}