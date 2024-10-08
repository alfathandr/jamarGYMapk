package com.example.jamargym.response;

public class LoginResponse {
    private int statusCode;  // Menggunakan int untuk statusCode
    private String message;
    private Data data;  // Menyimpan data token

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String token;

        public String getToken() {
            return token;
        }
    }
}
