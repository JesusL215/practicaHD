package com.smartpark.backend.pattern.decorator;

public class CarWashDecorator extends ServiceDecorator {

    public CarWashDecorator(IParkingCost parkingCost) {
        super(parkingCost);
    }

    @Override
    public double getCosto() {
        return super.getCosto() + 15.00;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Lavado de Vehículo";
    }
}