package com.example.logisticsoptimization;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private int id;
    private String model;
    private String plateNumber;
    private double fuelConsumption;
    private String fuelType;

    public Vehicle(int id, String model, String plateNumber, double fuelConsumption, String fuelType) {
        this.id = id;
        this.model = model;
        this.plateNumber = plateNumber;
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
    }

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
    public double getFuelConsumption() { return fuelConsumption; }
    public String getFuelType() { return fuelType; }
}
