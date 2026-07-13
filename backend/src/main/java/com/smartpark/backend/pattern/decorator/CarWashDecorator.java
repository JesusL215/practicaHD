package com.smartpark.backend.pattern.decorator;

public class CarWashDecorator extends ServiceDecorator {
    private final double costoLavado; // ¡NUEVO!

    public CarWashDecorator(IParkingCost parkingCost, double costoLavado) {
        super(parkingCost);
        this.costoLavado = costoLavado;
    }

    @Override
    public double getCosto() {
        return super.getCosto() + costoLavado; // Sumamos la tarifa dinámica
    }
}