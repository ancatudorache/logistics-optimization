package com.example.logisticsoptimization;

public class Delivery {
    private int id;
    private String pickupAddress;
    private String deliveryAddress;
    private String deadline;
    private double fuelConsumption;
    private String fuelType;
    private double fuelPrice;


    public Delivery(int id, String pickupAddress, String deliveryAddress, String deadline, double fuelConsumption, String fuelType, double fuelPrice) {
        this.id = id;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.deadline = deadline;
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
        this.fuelPrice = fuelPrice;
    }

    public int getId() { return id; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeadline() { return deadline; }

    public double getFuelConsumption() { return fuelConsumption; }
    public String getFuelType() { return fuelType; }
    public double getFuelPrice() { return fuelPrice; }

}
