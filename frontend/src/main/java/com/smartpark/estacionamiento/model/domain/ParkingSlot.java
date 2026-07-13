package com.smartpark.estacionamiento.model.domain;

public class ParkingSlot {
    private Long id;
    private String numero;
    private String estado;
    private String tipoVehiculoPermitido;

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoVehiculoPermitido() {
        return tipoVehiculoPermitido;
    }

    public void setTipoVehiculoPermitido(String tipoVehiculoPermitido) {
        this.tipoVehiculoPermitido = tipoVehiculoPermitido;
    }
}