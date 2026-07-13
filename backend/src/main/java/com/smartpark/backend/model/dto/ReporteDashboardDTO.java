package com.smartpark.backend.model.dto;

import java.util.List;
import java.util.Map;

public class ReporteDashboardDTO {

    // KPIs (Tarjetas superiores)
    private int vehiculosIngresadosHoy;
    private int vehiculosEstacionados;
    private int espaciosLibres;
    private double ingresosHoy;
    private double ingresosMes;

    // Gráficos (Mapas)
    private Map ingresosPorDia;
    private Map ingresosPorTipoVehiculo;
    private Map horasPico;

    // Tabla de Auditoría
    private List movimientosRecientes;

    public ReporteDashboardDTO() {}

    // Getters y Setters
    public int getVehiculosIngresadosHoy() { return vehiculosIngresadosHoy; }
    public void setVehiculosIngresadosHoy(int vehiculosIngresadosHoy) { this.vehiculosIngresadosHoy = vehiculosIngresadosHoy; }

    public int getVehiculosEstacionados() { return vehiculosEstacionados; }
    public void setVehiculosEstacionados(int vehiculosEstacionados) { this.vehiculosEstacionados = vehiculosEstacionados; }

    public int getEspaciosLibres() { return espaciosLibres; }
    public void setEspaciosLibres(int espaciosLibres) { this.espaciosLibres = espaciosLibres; }

    public double getIngresosHoy() { return ingresosHoy; }
    public void setIngresosHoy(double ingresosHoy) { this.ingresosHoy = ingresosHoy; }

    public double getIngresosMes() { return ingresosMes; }
    public void setIngresosMes(double ingresosMes) { this.ingresosMes = ingresosMes; }

    public Map getIngresosPorDia() { return ingresosPorDia; }
    public void setIngresosPorDia(Map ingresosPorDia) { this.ingresosPorDia = ingresosPorDia; }

    public Map getIngresosPorTipoVehiculo() { return ingresosPorTipoVehiculo; }
    public void setIngresosPorTipoVehiculo(Map ingresosPorTipoVehiculo) { this.ingresosPorTipoVehiculo = ingresosPorTipoVehiculo; }

    public Map getHorasPico() { return horasPico; }
    public void setHorasPico(Map horasPico) { this.horasPico = horasPico; }

    public List getMovimientosRecientes() { return movimientosRecientes; }
    public void setMovimientosRecientes(List movimientosRecientes) { this.movimientosRecientes = movimientosRecientes; }
}