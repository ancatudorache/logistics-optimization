package com.example.logisticsoptimization;

public class Delivery {
    private int id;
    private String pickupAddress;
    private String deliveryAddress;
    private String deadline;

    public Delivery(int id, String pickupAddress, String deliveryAddress, String deadline) {
        this.id = id;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeadline() { return deadline; }
}