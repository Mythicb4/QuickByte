package com.mycompany.quickbite.model;

/**
 * Representa un item individual dentro de una compra de estudiante.
 * Similar a SaleItem pero simplificado para persistencia.
 */
public class PurchaseItem {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public PurchaseItem() {
    }

    public PurchaseItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters y Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
