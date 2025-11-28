package com.mycompany.quickbite.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo que representa una Venta completa.
 * Incluye Cliente, Método de Pago y Monto Recibido.
 */
public class Venta {
    private String id;
    private String businessEmail;
    private LocalDateTime saleDate; 
    private double totalAmount;
    // --- Requerimiento funcional ---
    private String clientName; // Cliente
    private String paymentMethod; // Método de pago
    private double receivedAmount; // Dinero ingresado
    // -------------------------------
    private double changeAmount;
    private List<SaleItem> items;

    public Venta() {
        this.saleDate = LocalDateTime.now();
    }

    public Venta(String id, String businessEmail, double totalAmount, String paymentMethod, String clientName, double receivedAmount, double changeAmount, List<SaleItem> items) {
        this.id = id;
        this.businessEmail = businessEmail;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.clientName = clientName;
        this.receivedAmount = receivedAmount;
        this.changeAmount = changeAmount;
        this.items = items;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBusinessEmail() { return businessEmail; }
    public void setBusinessEmail(String businessEmail) { this.businessEmail = businessEmail; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    // --- Métodos del Requerimiento ---
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public double getReceivedAmount() { return receivedAmount; }
    public void setReceivedAmount(double receivedAmount) { this.receivedAmount = receivedAmount; }
    // ----------------------------------
    
    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}