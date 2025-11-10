package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;

public class TestProductService {
    public static void main(String[] args) {
        ProductService service = new ProductService(new ProductDao("data/products.json"));

        Product nuevo = new Product("Empanada de Pollo", 2000, "Fritos",
                "Empanada crujiente de pollo", 12,
                "images/empanada.png", 1000, "Fritos La Abuela", 5);

        service.createProduct(nuevo);
        System.out.println("Producto creado: " + nuevo.getId());

        System.out.println("\nProductos de bajo stock:");
        service.getLowStockProducts().forEach(System.out::println);

        nuevo.setStock(3);
        service.updateProduct(nuevo);
        System.out.println("\nProducto actualizado (bajo stock): " + nuevo);

        System.out.println("\nCantidad de productos activos: " + service.countAvailableProducts());
    }
}
