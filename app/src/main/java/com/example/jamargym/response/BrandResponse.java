package com.example.jamargym.response;

import java.util.List;

public class BrandResponse {
    private int statusCode;
    private String message;
    private Data data;

    // Getter untuk statusCode
    public int getStatusCode() {
        return statusCode;
    }

    // Getter untuk message
    public String getMessage() {
        return message;
    }

    // Getter untuk data
    public Data getData() {
        return data;
    }

    // Kelas Data di dalam BrandResponse
    public static class Data {
        private List<Brand> brands;

        // Getter untuk brands
        public List<Brand> getBrands() {
            return brands;
        }
    }

    // Kelas Brand di dalam Data
    public static class Brand {
        private int id;
        private String name;
        private String description;
        private String bank;
        private long rek;
        private String name_rek;
        private String image;

        // Getter untuk id
        public int getId() {
            return id;
        }

        // Getter untuk name
        public String getName() {
            return name;
        }

        // Getter untuk description
        public String getDescription() {
            return description;
        }

        // Getter untuk bank
        public String getBank() {
            return bank;
        }
    }
}
