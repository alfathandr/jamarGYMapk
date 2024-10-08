package com.example.jamargym.request;

public class PackageRequest {
    private String name;
    private String description;
    private int duration;
    private String price;

    // Constructor
    public PackageRequest(String name, String description, int duration, String price) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.price = price;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
