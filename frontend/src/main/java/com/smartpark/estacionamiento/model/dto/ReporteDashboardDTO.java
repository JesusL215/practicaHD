package com.smartpark.estacionamiento.model.dto;

import java.util.List;
import java.util.Map;

public class ReporteDashboardDTO {

    private int vehiculosIngresadosHoy;
    private int vehiculosEstacionados;
    private int espaciosLibres;
    private double ingresosHoy;
    private double ingresosMes;

    // <-- Agregar los tipos genéricos
    private Map<String, Double> ingresosPorDia;
    private Map<String, Double> ingresosPorTipoVehiculo;
    private Map<String, Integer> horasPico;

    private List<MovimientoDTO> movimientosRecientes;

    public ReporteDashboardDTO() {}

    public int getVehiculosIngresadosHoy() {
        return vehiculosIngresadosHoy;
    }

    public int getVehiculosEstacionados() {
        return vehiculosEstacionados;
    }

    public int getEspaciosLibres() {
        return espaciosLibres;
    }

    public double getIngresosHoy() {
        return ingresosHoy;
    }

    public double getIngresosMes() {
        return ingresosMes;
    }

    public Map<String, Double> getIngresosPorDia() {
        return ingresosPorDia;
    }

    public Map<String, Double> getIngresosPorTipoVehiculo() {
        return ingresosPorTipoVehiculo;
    }

    public Map<String, Integer> getHorasPico() {
        return horasPico;
    }

    public List<MovimientoDTO> getMovimientosRecientes() {
        return movimientosRecientes;
    }
}