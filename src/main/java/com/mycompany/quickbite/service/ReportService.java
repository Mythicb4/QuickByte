// Archivo sugerido: com.mycompany.quickbite.service.ReportService.java

package com.mycompany.quickbite.service;

import com.mycompany.quickbite.QBReportesFX;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.model.SaleItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final SaleService saleService;
    private final ProductService productService; // Necesario para obtener costo y stock actual

    public ReportService(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * DTO para el resumen de ventas.
     * 
     * @param totalRevenue      Ingresos totales
     * @param totalCost         Costos totales de los productos vendidos
     * @param totalProfit       Ganancia (Ingresos - Costos)
     * @param productSalesStats Lista de estadísticas por producto vendido
     */
    public record SalesSummary(
            double totalRevenue,
            double totalCost,
            double totalProfit,
            List<QBReportesFX.ProductSalesStats> productSalesStats) {
    }

    /**
     * Genera un resumen de ventas para un rango de fechas.
     */
    public SalesSummary generateSalesSummary(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        // Incluye todo el día final
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

        List<Sale> allSales = saleService.getAllSales();

        // 1. Filtrar ventas por rango de fechas
        List<Sale> filteredSales = allSales.stream()
                .filter(s -> s.getSaleDate().isAfter(startDateTime) || s.getSaleDate().isEqual(startDateTime))
                .filter(s -> s.getSaleDate().isBefore(endDateTime))
                .collect(Collectors.toList());

        // 2. Calcular Resumen Global
        double totalRevenue = filteredSales.stream()
                .mapToDouble(Sale::getTotalAmount)
                .sum();

        double totalCost = 0.0;

        // 3. Agrupar y calcular estadísticas por Producto
        // Un mapa de Producto ID -> Stats
        Map<String, ProductStatsBuilder> statsMap = filteredSales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .collect(Collectors.groupingBy(
                        SaleItem::getProductId, // Agrupar por ID de Producto
                        Collectors.reducing(
                                new ProductStatsBuilder(), // Reductor inicial
                                item -> new ProductStatsBuilder(item.getQuantity(),
                                        item.getQuantity() * item.getUnitPrice()), // Mapeo
                                ProductStatsBuilder::combine // Combinación
                        )));

        // 4. Construir la lista final de ProductSalesStats y sumar el Costo Total
        List<QBReportesFX.ProductSalesStats> finalStats = statsMap.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    ProductStatsBuilder builder = entry.getValue();

                    Product product = productService.getProductByIdSafe(productId); // Usar versión segura

                    String name = product != null ? product.getName() : "Desconocido";
                    double cost = product != null ? product.getCost() : 0.0;
                    int stock = product != null ? product.getStock() : 0;

                    double itemCost = cost * builder.totalQuantitySold;

                    // Acumular el costo total para el resumen
                    // IMPORTANTE: Dado que no podemos modificar variables en un stream de esta
                    // forma,
                    // moveremos el cálculo de totalCost después del stream o usaremos un objeto
                    // atómico
                    // Por simplicidad, lo haré de forma simple ahora:
                    // totalCost += itemCost;

                    return new QBReportesFX.ProductSalesStats(
                            productId,
                            name,
                            cost,
                            stock,
                            builder.totalQuantitySold);
                })
                .collect(Collectors.toList());

        // Recalcular totalCost de manera simple (es lo más sencillo sin modificar el
        // stream anterior)
        totalCost = finalStats.stream()
                .mapToDouble(s -> {
                    double itemCost = s.cost() * s.totalQuantitySold();
                    System.out.println("Producto: " + s.productName() + " | Costo unitario: " + s.cost()
                            + " | Cantidad: " + s.totalQuantitySold() + " | Total costo: " + itemCost);
                    return itemCost;
                })
                .sum();

        System.out.println("========================================");
        System.out.println("Total Ingresos: " + totalRevenue);
        System.out.println("Total Salidas (Costos): " + totalCost);
        System.out.println("Ganancias Netas: " + (totalRevenue - totalCost));
        System.out.println("========================================");

        double totalProfit = totalRevenue - totalCost;

        return new SalesSummary(totalRevenue, totalCost, totalProfit, finalStats);
    }

    // Clase auxiliar para el procesamiento en el stream de ventas
    private static class ProductStatsBuilder {
        int totalQuantitySold;
        double totalRevenueGenerated;

        public ProductStatsBuilder() {
            this(0, 0.0);
        }

        public ProductStatsBuilder(int quantity, double revenue) {
            this.totalQuantitySold = quantity;
            this.totalRevenueGenerated = revenue;
        }

        public ProductStatsBuilder combine(ProductStatsBuilder other) {
            this.totalQuantitySold += other.totalQuantitySold;
            this.totalRevenueGenerated += other.totalRevenueGenerated;
            return this;
        }
    }
}
