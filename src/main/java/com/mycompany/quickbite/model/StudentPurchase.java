package com.mycompany.quickbite.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo que representa una Compra de un Estudiante.
 * Se guarda cuando el estudiante genera el QR para pagar.
 */
public class StudentPurchase {
    private String id;
    private String studentEmail;
    private String businessName;
    private String businessLocation;
    private LocalDateTime purchaseDate;
    private double totalAmount;
    private String paymentMethod; // Siempre "QR" para estudiantes
    private List<PurchaseItem> items;

    public StudentPurchase() {
        this.purchaseDate = LocalDateTime.now();
        this.paymentMethod = "QR";
    }

    public StudentPurchase(String id, String studentEmail, String businessName, String businessLocation,
            LocalDateTime purchaseDate, double totalAmount, List<PurchaseItem> items) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.businessName = businessName;
        this.businessLocation = businessLocation;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = "QR";
        this.items = items;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }
}
