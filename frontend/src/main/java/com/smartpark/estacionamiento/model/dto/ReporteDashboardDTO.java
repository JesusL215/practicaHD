package com.smartpark.estacionamiento.model.dto;

import java.util.List;
import java.util.Map;

public class ReporteDashboardDTO {

    private int vehiculosIngresadosHoy;
    private int vehiculosEstacionados;
    private int espaciosLibres;
    private double ingresosHoy;
    private double ingresosMes;

    private Map ingresosPorDia;
    private Map ingresosPorTipoVehiculo;
    private Map horasPico;

    private List movimientosRecientes;

    public ReporteDashboardDTO() {}

    public int getVehiculosIngresadosHoy() { return vehiculosIngresadosHoy; }
    public int getVehiculosEstacionados() { return vehiculosEstacionados; }
    public int getEspaciosLibres() { return espaciosLibres; }
    public double getIngresosHoy() { return ingresosHoy; }
    public double getIngresosMes() { return ingresosMes; }
    public Map getIngresosPorDia() { return ingresosPorDia; }
    public Map getIngresosPorTipoVehiculo() { return ingresosPorTipoVehiculo; }
    public Map getHorasPico() { return horasPico; }
    public List getMovimientosRecientes() { return movimientosRecientes; }
}