package com.example.jamargym.response;

import java.util.List;

public class UserResponse {
    private int statusCode;
    private String message;
    private User data;

    // Getter untuk statusCode
    public int getStatusCode() {
        return statusCode;
    }

    // Getter untuk message
    public String getMessage() {
        return message;
    }

    // Getter untuk data
    public User getData() {
        return data;
    }

    // Kelas User di dalam UserResponse
    public static class User {
        private String name;
        private String email;
        private String address;
        private String qrcode;
        private String image;
        private List<PackageData> packages;

        // Getter untuk name
        public String getName() {
            return name;
        }

        // Getter untuk email
        public String getEmail() {
            return email;
        }

        // Getter untuk address
        public String getAddress() {
            return address;
        }

        // Getter untuk qrcode
        public String getQRCode() {
            return qrcode;
        }

        // Getter untuk image
        public String getImage() {
            return image;
        }

        // Getter untuk packages
        public List<PackageData> getPackages() {
            return packages;
        }

        // Kelas PackageData di dalam User
        public static class PackageData {
            private String end_date;
            private String price;
            private String image;
            private String status;
            private PackageDetail packageDetail;

            // Getter untuk endDate
            public String getEndDate() {
                return end_date;
            }

            // Getter untuk price
            public String getPrice() {
                return price;
            }

            // Getter untuk image
            public String getImage() {
                return image;
            }

            // Getter untuk status
            public String getStatus() {
                return status;
            }

            // Getter untuk packageDetail
            public PackageDetail getPackageDetail() {
                return packageDetail;
            }

            // Kelas PackageDetail di dalam PackageData
            public static class PackageDetail {
                private String name;
                private String description;
                private String price;

                // Getter untuk name
                public String getName() {
                    return name;
                }

                // Getter untuk description
                public String getDescription() {
                    return description;
                }

                // Getter untuk price
                public String getPrice() {
                    return price;
                }
            }
        }
    }
}
