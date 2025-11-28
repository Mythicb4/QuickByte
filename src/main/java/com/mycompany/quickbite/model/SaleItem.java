package com.mycompany.quickbite.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Clase para representar un artículo dentro de la venta (una fila de la tabla)
public class SaleItem {

    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final DoubleProperty unitPrice;
    private final DoubleProperty subtotal;
    private final Product product; // Referencia al producto original

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.productName = new SimpleStringProperty(product.getName());
        this.unitPrice = new SimpleDoubleProperty(product.getPrice());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subtotal = new SimpleDoubleProperty(product.getPrice() * quantity);
    }

    // Método para actualizar la cantidad y recalcular el subtotal
    public void updateQuantity(int newQuantity) {
        this.quantity.set(newQuantity);
        this.subtotal.set(this.unitPrice.get() * newQuantity);
    }

    // ---------- GETTERS ADICIONALES NECESARIOS PARA LA VENTA ----------

    // Getter para obtener el ID del producto original ⬅️ ESTE ES EL MÉTODO FALTANTE
    public String getProductId() {
        return product.getId();
    }
    
    // Getter para obtener el objeto Product original
    public Product getProduct() {
        return product;
    }

    // ---------- GETTERS PARA JAVAFX PROPERTIES ----------
    
    public String getProductName() {
        return productName.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public double getUnitPrice() {
        return unitPrice.get();
    }

    public double getSubtotal() {
        return subtotal.get();
    }
    
    // Y sus Property methods
    public StringProperty productNameProperty() {
        return productName;
    }
    public IntegerProperty quantityProperty() {
        return quantity;
    }
    public DoubleProperty unitPriceProperty() {
        return unitPrice;
    }
    public DoubleProperty subtotalProperty() {
        return subtotal;
    }
    
    @Override
    public String toString() {
        return productName.get();
    }
}