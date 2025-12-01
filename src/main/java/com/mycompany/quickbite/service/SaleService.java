package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.model.Sale;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

public class SaleService {

    private final SaleDao saleDao; // ⬅️ CAMBIO: Renombrado de ventaDao a saleDao
    private final ProductService productService;

    public SaleService(SaleDao saleDao, ProductService productService) { // ⬅️ CAMBIO: Parámetro corregido
        this.saleDao = saleDao;
        this.productService = productService;
    }

    /**
     * Registra una nueva venta, genera ID y actualiza el stock de los productos.
     * @param sale La venta a registrar.
     * @return La venta registrada (con ID generado).
     * @throws Exception Si hay problemas con stock o la base de datos.
     */
    public Sale registerSale(Sale sale) throws Exception {
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }
        if (sale.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("El total de la venta debe ser positivo.");
        }
        
        // 1. Validar y actualizar stock de productos
        // ❌ Línea 33 anterior: productService.updateStockForSale(sale.getItems());
        // ✅ SOLUCIÓN: Iterar y llamar al método updateStock() por cada artículo
        for (SaleItem item : sale.getItems()) {
            // El método updateStock debe restar la cantidad vendida (por eso la cantidad es negativa)
            productService.updateStock(item.getProductId(), -item.getQuantity()); 
        }

        // 2. Guardar la venta
        saleDao.addSale(sale);
        
        return sale;
    }
    
    // Nuevo método en SaleService
    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        // El fin del día es un segundo antes de la medianoche del día siguiente
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return saleDao.loadAll().stream()
                .filter(sale -> !sale.getSaleDate().isBefore(start) && !sale.getSaleDate().isAfter(end))
                .collect(Collectors.toList());
    }
    
    public List<Sale> getAllSales() { 
        List<Sale> allSales = saleDao.loadAll(); 
        
        // 🔑 PASO CLAVE: Buscar los detalles del producto y asignarlos a SaleItem
        for (Sale sale : allSales) {
            loadProductDetails(sale);
        }
        
        return allSales;
    }
    
    private void loadProductDetails(Sale sale) {
        if (sale.getItems() == null) {
            return;
        }
        
        for (SaleItem item : sale.getItems()) {
            // 1. Buscar el producto usando el ID guardado en el JSON
            Product product = productService.getProductById(item.getProductId());
            
            // 2. Rellenar las propiedades FX del SaleItem
            item.setProductDetails(product); 
        }
    }
}