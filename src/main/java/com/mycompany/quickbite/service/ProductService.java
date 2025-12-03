package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.SaleItem;

import java.util.*;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Product createProduct(Product product) {
        Objects.requireNonNull(product, "El producto no puede ser nulo");

        product.validateForSave();

        boolean existsByName = productDao.loadAll().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));

        if (existsByName) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + product.getName());
        }

        return productDao.addProduct(product);
    }

    public Product updateProduct(Product updated) {
        Objects.requireNonNull(updated, "El producto actualizado no puede ser nulo");
        updated.validateForSave();

        if (!productDao.existsById(updated.getId())) {
            throw new NoSuchElementException("No existe un producto con el ID: " + updated.getId());
        }

        return productDao.updateProduct(updated);
    }

    public void deleteProduct(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }

        if (!productDao.existsById(id)) {
            throw new NoSuchElementException("El producto no existe: " + id);
        }

        productDao.removeProduct(id);
    }

    public Product disableProduct(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }

        if (!productDao.existsById(id)) {
            throw new NoSuchElementException("El producto no existe: " + id);
        }

        return productDao.disableProduct(id);
    }

    public List<Product> getAllProducts() {
        return productDao.loadAll();
    }

    public Product getProductById(String id) throws NoSuchElementException { // ⬅️ MÉTODO AÑADIDO
        return productDao.loadAll().stream() // Asume loadAll() es accesible en ProductDao
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto con ID " + id + " no encontrado."));
    }

    // Versión segura que retorna null en lugar de lanzar excepción
    public Product getProductByIdSafe(String id) {
        try {
            return productDao.loadAll().stream()
                    .filter(p -> id.equals(p.getId()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Product> getProductsByCategory(String category) {
        return productDao.findByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        return productDao.loadAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public long countAvailableProducts() {
        return productDao.loadAll().stream()
                .filter(Product::isAvailable)
                .count();
    }

    public long countDisabledProducts() {
        return productDao.loadAll().stream()
                .filter(p -> !p.isEnabled())
                .count();
    }

    // Verifica este bloque en ProductService.java
    public void updateStock(String productId, int quantityChange) throws Exception {
        // 1. Cargar el producto usando la versión segura
        Product product = getProductByIdSafe(productId);

        // Validar si el producto existe
        if (product == null) {
            System.err.println(
                    "Advertencia: Producto con ID " + productId + " no encontrado. Omitiendo actualización de stock.");
            return; // Retornar sin error para no romper el flujo
        }

        // 2. Calcular nuevo stock
        int newStock = product.getStock() + quantityChange;

        // 3. Validar stock insuficiente (solo cuando quantityChange es negativo, es
        // decir, una venta)
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName() +
                    ". Disponible: " + product.getStock() + ", Solicitado: " + (-quantityChange));
        }

        // 4. Actualizar y guardar
        product.setStock(newStock);
        productDao.updateProduct(product);
    }
}
