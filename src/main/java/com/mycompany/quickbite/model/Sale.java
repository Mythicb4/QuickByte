package com.mycompany.quickbite.model;

import java.time.LocalDateTime;
import java.util.List;
import com.mycompany.quickbite.util.SessionContext; // Necesaria para obtener el email

/**
 * Modelo que representa una Venta completa.
 * Incluye Cliente, Método de Pago y Monto Recibido.
 */
public class Sale { 
    private String id;
    private String businessEmail;
    private LocalDateTime saleDate; 
    private double totalAmount;
    
    // --- Requerimiento funcional ---
    private String clientName; 
    private String paymentMethod; 
    private double receivedAmount; // ⬅️ Campo necesario
    // ------------------------------
    
    private double change; // ⬅️ Campo necesario
    private List<SaleItem> items;

    public Sale() {
        this.saleDate = LocalDateTime.now();
    }

    // Nuevo Constructor para coincidir con QBVentaFX (7 parámetros)
    public Sale(String id, LocalDateTime saleDate, String clientIdentifier, double totalAmount, double receivedAmount, double change, List<SaleItem> items) {
        this.id = id;
        this.saleDate = saleDate; 
        this.totalAmount = totalAmount;
        this.receivedAmount = receivedAmount;
        this.change = change;
        this.items = items;

        // --- Asignación de campos faltantes/reemplazados ---
        // 1. businessEmail: Se obtiene del contexto de sesión
        this.businessEmail = SessionContext.getLoggedInBusinessEmail(); 

        // 2. clientName: Usamos el identificador
        this.clientName = clientIdentifier;
        
        // 3. paymentMethod: Como no se pasa, usamos un valor por defecto.
        this.paymentMethod = "Efectivo"; 
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

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    // 🔑 MÉTODOS CLAVE FALTANTES PARA LA COMPILACIÓN
    public double getReceivedAmount() { return receivedAmount; } // ⬅️ ¡AGREGADO!
    public void setReceivedAmount(double receivedAmount) { this.receivedAmount = receivedAmount; } // ⬅️ ¡AGREGADO!
    
    public double getChange() { return change; } // ⬅️ ¡AGREGADO!
    public void setChange(double change) { this.change = change; } // ⬅️ ¡AGREGADO!
    
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}