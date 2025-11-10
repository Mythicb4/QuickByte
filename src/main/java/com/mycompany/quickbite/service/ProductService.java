package com.mycompany.quickbite.service;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;

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

    public Optional<Product> getProductById(String id) {
        return productDao.findById(id);
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
}
