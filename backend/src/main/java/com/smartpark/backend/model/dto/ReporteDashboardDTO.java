package com.smartpark.backend.model.dto;

import java.util.Map;

public class ReporteDashboardDTO {
    private Map ingresosPorDia;
    private Map ingresosPorTipoVehiculo;

    public ReporteDashboardDTO(Map ingresosPorDia, Map ingresosPorTipoVehiculo) {
        this.ingresosPorDia = ingresosPorDia;
        this.ingresosPorTipoVehiculo = ingresosPorTipoVehiculo;
    }

    public Map getIngresosPorDia() { return ingresosPorDia; }
    public void setIngresosPorDia(Map ingresosPorDia) { this.ingresosPorDia = ingresosPorDia; }
    public Map getIngresosPorTipoVehiculo() { return ingresosPorTipoVehiculo; }
    public void setIngresosPorTipoVehiculo(Map ingresosPorTipoVehiculo) { this.ingresosPorTipoVehiculo = ingresosPorTipoVehiculo; }
}