package com.example.logisticsoptimization;

public class Vehicle {
    private int id;
    private String model;
    private String plateNumber;

    public Vehicle(int id, String model, String plateNumber) {
        this.id = id;
        this.model = model;
        this.plateNumber = plateNumber;
    }

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
}