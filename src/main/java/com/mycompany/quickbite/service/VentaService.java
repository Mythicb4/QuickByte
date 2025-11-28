package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.VentaDao;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.model.Venta;

import java.util.List;

public class VentaService {

    private final VentaDao ventaDao;
    private final ProductService productService; // Necesario para actualizar stock

    public VentaService(VentaDao ventaDao, ProductService productService) {
        this.ventaDao = ventaDao;
        this.productService = productService;
    }

    /**
     * Registra una nueva venta, genera ID y actualiza el stock de los productos.
     */
    public Venta registerSale(Venta venta) throws Exception {
        if (venta.getItems() == null || venta.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }
        if (venta.getTotalAmount() <= 0) {
             throw new IllegalArgumentException("El total de la venta debe ser positivo.");
        }
        
        // 1. Validar y actualizar stock de productos
        // Se llama al ProductService para restar la cantidad vendida
        for (SaleItem item : venta.getItems()) {
            // El método updateStock debe restar la cantidad vendida (por eso es negativo)
            productService.updateStock(item.getProductId(), -item.getQuantity()); 
        }

        // 2. Generar ID y guardar la venta
        venta.setId(ventaDao.generateId());
        ventaDao.addVenta(venta);
        
        return venta;
    }
    
    public List<Venta> getAllVentas() {
        return ventaDao.loadAll();
    }
}