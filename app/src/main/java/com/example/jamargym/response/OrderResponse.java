package com.example.jamargym.response;

public class OrderResponse {
    private int package_id;
    private String image;
    private String message;

    // Default constructor for Retrofit
    public OrderResponse() {}

    public OrderResponse(int package_id, String image, String message) {
        this.package_id = package_id;
        this.image = image;
        this.message = message;
    }

    public int getPackageId() {
        return package_id;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }
}
