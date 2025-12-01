package com.mycompany.quickbite.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Clase para representar un artículo dentro de la venta (una fila de la tabla)
public class SaleItem {
    
    // --- Campos de PROPIEDADES (transient para ignorar en JSON y evitar el error GSON) ---
    private final transient StringProperty productName; // ⬅️ transient agregado
    private final transient IntegerProperty quantity;   // ⬅️ transient agregado
    private final transient DoubleProperty unitPrice;    // ⬅️ transient agregado
    private final transient DoubleProperty subtotal;     // ⬅️ transient agregado
    private final transient Product product; // ⬅️ transient agregado (solo es una referencia en runtime)
    
    // --- Campos de DATOS (Persisten en JSON) ---
    private String productIdForPersistence; 
    
    private String nameForPersistence; 
    private int quantityForPersistence;
    private double unitPriceForPersistence;
    private double subtotalForPersistence;
    
    public SaleItem() {
        this.product = null;
        this.productName = new SimpleStringProperty(""); // ⬅️ ¡INICIALIZADO!
        this.quantity = new SimpleIntegerProperty(0);   // ⬅️ ¡INICIALIZADO!
        this.unitPrice = new SimpleDoubleProperty(0.0); // ⬅️ ¡INICIALIZADO!
        this.subtotal = new SimpleDoubleProperty(0.0);  // ⬅️ ¡INICIALIZADO!
    }
    
    // Constructor 1: Usado al cargar desde JSON o al crear una venta manual sin el objeto Product completo
    public SaleItem(String productId, String productName, int quantity, double unitPrice) {
        this.productIdForPersistence = productId;
        
        // Asignar a los campos de persistencia
        this.nameForPersistence = productName; 
        this.quantityForPersistence = quantity;
        this.unitPriceForPersistence = unitPrice;
        this.subtotalForPersistence = unitPrice * quantity;

        // Inicializar las propiedades finales de FX
        this.product = null; 
        this.productName = new SimpleStringProperty(productName);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subtotal = new SimpleDoubleProperty(unitPrice * quantity);
    }

    public <T extends Product> void setProductDetails(T product) {
        if (product != null) {
            this.productName.set(product.getName());
            this.unitPrice.set(product.getPrice());
            
            // ASUNCIÓN: Si el JSON solo tiene el ID, asumimos que cada ID es 1 unidad 
            // y que el subtotal es igual al precio unitario.
            this.quantity.set(1); 
            this.subtotal.set(product.getPrice()); 
        } else {
            this.productName.set("Producto Desconocido (ID: " + productIdForPersistence + ")");
            this.unitPrice.set(0.0);
            this.quantity.set(0);
            this.subtotal.set(0.0);
        }
    }
    
    // Constructor 2: Usado cuando se añade un objeto Product completo (ej. desde un ComboBox)
    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.productIdForPersistence = product.getId();
        this.productName = new SimpleStringProperty(product.getName());
        
        // 🔑 CORRECCIÓN DE COMPILACIÓN: Se usa getPrice() de Product
        double price = product.getPrice(); // ⬅️ ¡CORREGIDO!
        
        this.unitPrice = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subtotal = new SimpleDoubleProperty(price * quantity); // ⬅️ ¡CORREGIDO!
    }

    // --- Métodos de Negocio ---
    
    public void setQuantity(int newQuantity) {
        this.quantityForPersistence = newQuantity; // Guardar en persistencia
        this.quantity.set(newQuantity); // Actualizar Property
        double newSubtotal = this.unitPriceForPersistence * newQuantity;
        this.subtotalForPersistence = newSubtotal; // Guardar en persistencia
        this.subtotal.set(newSubtotal); // Actualizar Property
    }

    // ---------- GETTERS ADICIONALES NECESARIOS PARA LA VENTA ----------

    public String getProductId() { return productIdForPersistence; }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getSubtotal() { return subtotal.get(); }
    
    // Propiedades JavaFX (sin cambios)
    public StringProperty productNameProperty() { return productName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty subtotalProperty() { return subtotal; }
}