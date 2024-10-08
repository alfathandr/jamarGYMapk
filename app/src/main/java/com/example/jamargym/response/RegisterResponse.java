package com.example.jamargym.response;

public class RegisterResponse {
    private int statusCode;
    private String message;
    private Data data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String name;  // Changed from 'username' to 'name'
        private String email;
        private String role;
        private String token;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getToken() {
            return token;
        }
    }
}
