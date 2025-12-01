package com.mycompany.quickbite.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Modelo DTO para el resumen financiero de un período.
 * Usa JavaFX Properties para facilitar el binding con la UI (Label/Text).
 */
public class FinancialReport {

    // Total de Ingresos (Ventas totales al cliente)
    private final DoubleProperty totalIngresos; 
    
    // Total de Salidas / Costo de los Productos Vendidos (COGS)
    private final DoubleProperty totalSalidas; 
    
    // Ganancia Neta (Ingresos - Salidas/Costos)
    private final DoubleProperty totalGananciaNeta; 

    public FinancialReport() {
        this.totalIngresos = new SimpleDoubleProperty(0.0);
        this.totalSalidas = new SimpleDoubleProperty(0.0);
        this.totalGananciaNeta = new SimpleDoubleProperty(0.0);
    }
    
    /**
     * Constructor para inicializar todos los valores. 
     * Recalcula la Ganancia Neta automáticamente.
     */
    public FinancialReport(double totalIngresos, double totalSalidas) {
        this.totalIngresos = new SimpleDoubleProperty(totalIngresos);
        this.totalSalidas = new SimpleDoubleProperty(totalSalidas);
        this.totalGananciaNeta = new SimpleDoubleProperty(totalIngresos - totalSalidas);
    }

    // --- Getters y Setters ---

    public double getTotalIngresos() {
        return totalIngresos.get();
    }
    
    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos.set(totalIngresos);
        // Recalcular la ganancia al cambiar el ingreso
        recalculateNetProfit(); 
    }

    public double getTotalSalidas() {
        return totalSalidas.get();
    }
    
    public void setTotalSalidas(double totalSalidas) {
        this.totalSalidas.set(totalSalidas);
        // Recalcular la ganancia al cambiar el costo
        recalculateNetProfit(); 
    }

    public double getTotalGananciaNeta() {
        return totalGananciaNeta.get();
    }
    
    // Set privado, solo se recalcula internamente
    private void setTotalGananciaNeta(double totalGananciaNeta) {
        this.totalGananciaNeta.set(totalGananciaNeta);
    }
    
    private void recalculateNetProfit() {
         this.totalGananciaNeta.set(getTotalIngresos() - getTotalSalidas());
    }

    // --- JavaFX Property Getters para el binding ---
    // Usaremos estas properties para bindear a los Text/Label en QBReportesFX.java

    public DoubleProperty totalIngresosProperty() {
        return totalIngresos;
    }

    public DoubleProperty totalSalidasProperty() {
        return totalSalidas;
    }

    public DoubleProperty totalGananciaNetaProperty() {
        return totalGananciaNeta;
    }
}
