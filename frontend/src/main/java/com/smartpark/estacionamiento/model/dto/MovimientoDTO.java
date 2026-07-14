package com.smartpark.estacionamiento.model.dto;

public class MovimientoDTO {
    private String fecha;
    private String hora;
    private String placa;
    private String tipoVehiculo;
    private String estado;
    private double montoPagado;

    public MovimientoDTO(String fecha, String hora, String placa, String tipoVehiculo, String estado, double montoPagado) {
        this.fecha = fecha;
        this.hora = hora;
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.estado = estado;
        this.montoPagado = montoPagado;
    }

    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getPlaca() { return placa; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public String getEstado() { return estado; }
    public double getMontoPagado() { return montoPagado; }
}