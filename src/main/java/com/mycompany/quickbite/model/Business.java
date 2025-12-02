package com.mycompany.quickbite.model;

public class Business {
    private String businessName;
    private String location;
    private String email;
    private String password;
    private String imagePath;

    public Business() {
    }

    public Business(String businessName, String location, String email, String password) {
        this.businessName = businessName;
        this.location = location;
        this.email = email;
        this.password = password;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
