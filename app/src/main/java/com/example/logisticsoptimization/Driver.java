package com.example.logisticsoptimization;

public class Driver {
    private int id;
    private String name;
    private String username;

    public Driver(int id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
}