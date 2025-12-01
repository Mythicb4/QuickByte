package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.FinancialReport;
import com.mycompany.quickbite.model.FullReport;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.model.SaleItemReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio para generar reportes financieros y de ítems más/menos vendidos
 * a partir de los datos de ventas en un rango de fechas.
 */
public class ReportGenerator {

    private final SaleDao saleDao;
    private final ProductService productService; 
    private static final int RANKING_LIMIT = 5; // Limite para Top y Bottom 5

    public ReportGenerator(SaleDao saleDao, ProductService productService) {
        this.saleDao = Objects.requireNonNull(saleDao, "SaleDao no puede ser nulo");
        this.productService = Objects.requireNonNull(productService, "ProductService no puede ser nulo");
    }

    /**
     * Genera el reporte completo de ventas y financiero para un rango de fechas (inclusivo).
     * @param startDate La fecha de inicio del rango (inclusive).
     * @param endDate La fecha de fin del rango (inclusive).
     * @return Un objeto FullReport que contiene los datos financieros y el ranking de ítems.
     */
    public FullReport generateReports(LocalDate startDate, LocalDate endDate) {
        
        // 1. Establecer el rango de tiempo completo
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); 

        // 2. Obtener y filtrar ventas por el rango de fechas
        List<Sale> allSales = saleDao.loadAll();
        List<Sale> filteredSales = allSales.stream()
            .filter(sale -> !sale.getSaleDate().isBefore(startDateTime) && !sale.getSaleDate().isAfter(endDateTime))
            .collect(Collectors.toList());

        // Manejar caso sin ventas en el rango
        if (filteredSales.isEmpty()) {
             System.out.println("No hay ventas en el rango de fechas especificado.");
             return new FullReport(new FinancialReport(), new ArrayList<>(), new ArrayList<>());
        }

        // --- Estructuras para agregación y cálculo financiero ---
        double totalIngresos = 0.0;
        double totalSalidas = 0.0; // Costo de los bienes vendidos (COGS)
        
        // Mapa para el ranking: { productID -> SaleItemReport }
        Map<String, SaleItemReport> itemReportMap = filteredSales.stream()
            .flatMap(sale -> sale.getItems().stream()) 
            .collect(Collectors.groupingBy(
                SaleItem::getProductId,
                Collectors.reducing(
                    // Identidad
                    new SaleItemReport("", 0, 0.0), 
                    // Mapeo (para cada SaleItem)
                    (item) -> {
                        // Creando un SaleItemReport para acumulación
                        return new SaleItemReport(
                            item.getProductName(), 
                            item.getQuantity(),     
                            item.getUnitPrice()     
                        );
                    },
                    // Combinador: Suma las cantidades vendidas
                    (report1, report2) -> new SaleItemReport(
                        report1.getProductName().isEmpty() ? report2.getProductName() : report1.getProductName(), 
                        report1.getTotalQuantitySold() + report2.getTotalQuantitySold(), 
                        report1.getUnitPrice()
                    )
                )
            ));

        // 3. Cálculo de Ingresos y Costos (Salidas)
        for (Sale sale : filteredSales) {
            // Ingresos = Suma de los totales de venta
            totalIngresos += sale.getTotalAmount();
            
            for (SaleItem item : sale.getItems()) {
                try {
                    // **CRÍTICO:** Se usa ProductService para obtener el COSTO actual del producto.
                    Product product = productService.getProductById(item.getProductId());
                    // Salidas = Costo del producto * Cantidad vendida
                    totalSalidas += product.getCost() * item.getQuantity();
                } catch (NoSuchElementException e) {
                    System.err.println("Advertencia: Producto con ID " + item.getProductId() + " no encontrado para calcular el costo. Asumiendo costo 0.0.");
                    // Si el producto fue eliminado o el ID es inválido, asumimos costo cero.
                }
            }
        }
        
        FinancialReport financialReport = new FinancialReport(totalIngresos, totalSalidas);

        // 4. Procesamiento para Ranking
        List<SaleItemReport> allRankedItems = new ArrayList<>(itemReportMap.values());

        // 5. Ranking (Más vendidos: Orden descendente por cantidad vendida)
        List<SaleItemReport> topSellingItems = allRankedItems.stream()
                .sorted(Comparator.comparing(SaleItemReport::getTotalQuantitySold).reversed())
                .limit(RANKING_LIMIT)
                .collect(Collectors.toList());

        // 6. Ranking (Menos vendidos: Orden ascendente por cantidad vendida)
        List<SaleItemReport> leastSellingItems = allRankedItems.stream()
                .sorted(Comparator.comparing(SaleItemReport::getTotalQuantitySold))
                .limit(RANKING_LIMIT)
                .collect(Collectors.toList());

        // 7. Retornar el resultado completo
        return new FullReport(financialReport, topSellingItems, leastSellingItems);
    }
}
