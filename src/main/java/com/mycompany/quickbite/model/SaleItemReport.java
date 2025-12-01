package com.mycompany.quickbite.model;

/**
 * Modelo DTO (Data Transfer Object) para los ítems en los reportes de ranking.
 * Contiene el nombre del producto y la cantidad total vendida en el período.
 */
public class SaleItemReport {

    private String productName;
    private int totalQuantitySold;
    
    // Opcional, pero útil para ordenar y para tener un precio de referencia
    private double unitPrice; 

    public SaleItemReport(String productName, int totalQuantitySold, double unitPrice) {
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
        this.unitPrice = unitPrice;
    }

    // --- Getters requeridos por JavaFX TableView (PropertyValueFactory) ---
    
    public String getProductName() {
        return productName;
    }

    // Usaremos este getter para mapear a las columnas 'Stock' y 'Stock1' en el FXML
    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
    
    // --- Setters (Opcionales, pero buena práctica si el DTO es mutable) ---

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setTotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
