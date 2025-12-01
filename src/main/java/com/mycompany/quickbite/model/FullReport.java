package com.mycompany.quickbite.model;

import java.util.List;

/**
 * Contenedor (DTO) para los resultados completos del reporte.
 * Agrupa el FinancialReport y los dos rankings de SaleItemReport.
 */
public class FullReport {
    private final FinancialReport financialReport;
    private final List<SaleItemReport> topSellingItems;
    private final List<SaleItemReport> leastSellingItems;

    public FullReport(FinancialReport financialReport, List<SaleItemReport> topSellingItems, List<SaleItemReport> leastSellingItems) {
        this.financialReport = financialReport;
        this.topSellingItems = topSellingItems;
        this.leastSellingItems = leastSellingItems;
    }

    // Getters
    public FinancialReport getFinancialReport() {
        return financialReport;
    }

    public List<SaleItemReport> getTopSellingItems() {
        return topSellingItems;
    }

    public List<SaleItemReport> getLeastSellingItems() {
        return leastSellingItems;
    }
}
