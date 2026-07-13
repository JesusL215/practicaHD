package com.smartpark.estacionamiento.model.dto;

import java.util.Map;

public class ReporteDashboardDTO {

    // CORRECCIÓN: Aquí es donde faltaban los tipos <String, Double>
    private Map<String, Double> ingresosPorDia;
    private Map<String, Double> ingresosPorTipoVehiculo;

    public ReporteDashboardDTO() {
    }

    public ReporteDashboardDTO(Map<String, Double> ingresosPorDia, Map<String, Double> ingresosPorTipoVehiculo) {
        this.ingresosPorDia = ingresosPorDia;
        this.ingresosPorTipoVehiculo = ingresosPorTipoVehiculo;
    }

    public Map<String, Double> getIngresosPorDia() {
        return ingresosPorDia;
    }

    public void setIngresosPorDia(Map<String, Double> ingresosPorDia) {
        this.ingresosPorDia = ingresosPorDia;
    }

    public Map<String, Double> getIngresosPorTipoVehiculo() {
        return ingresosPorTipoVehiculo;
    }

    public void setIngresosPorTipoVehiculo(Map<String, Double> ingresosPorTipoVehiculo) {
        this.ingresosPorTipoVehiculo = ingresosPorTipoVehiculo;
    }
}