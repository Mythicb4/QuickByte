package com.mycompany.quickbite.model;

import java.util.Objects;

/**
 * Modelo que representa un producto del sistema.
 * POJO simple, compatible con Gson para serialización JSON.
 */
public class Product {

    private static final int DEFAULT_MIN_STOCK = 10;

    private String id;
    private String name;
    private double price;
    private String category;
    private String description;
    private int stock;
    private String imagePath;
    private double cost;
    private String supplier;
    private boolean enabled;
    private int minStock;

    public Product() { 
        // requerido por Gson
        this.minStock = DEFAULT_MIN_STOCK;
    }

    public Product(String name, double price, String category, String description,
                   int stock, String imagePath, double cost, String supplier, int minStock) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.stock = stock;                // <- se asigna correctamente
        this.imagePath = imagePath;
        this.cost = cost;
        this.supplier = supplier;
        this.minStock = minStock > 0 ? minStock : DEFAULT_MIN_STOCK;
        this.enabled = this.stock > 0;
    }

    // ---------- Getters ----------
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
    public String getImagePath() { return imagePath; }
    public double getCost() { return cost; }
    public String getSupplier() { return supplier; }
    public boolean isEnabled() { return enabled; }
    public int getMinStock() { return minStock; }

    // ---------- Setters ----------
    /**
     * setId debería usarse sólo desde DAO/Service al crear el producto.
     */
    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    /**
     * Asigna precio; no permite valores negativos.
     */
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price must be >= 0");
        this.price = price;
    }

    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Asigna stock y actualiza enabled.
     */
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock must be >= 0");
        this.stock = stock;
        updateEnabledFlag();
    }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    /**
     * Costo solo visible para usuario negocio; no permite negativos.
     */
    public void setCost(double cost) {
        if (cost < 0) throw new IllegalArgumentException("Cost must be >= 0");
        this.cost = cost;
    }

    public void setSupplier(String supplier) { this.supplier = supplier; }

    /**
     * Umbral mínimo para considerar "bajo stock".
     */
    public void setMinStock(int minStock) {
        if (minStock < 0) throw new IllegalArgumentException("minStock must be >= 0");
        this.minStock = minStock;
    }

    // ---------- Operaciones sobre stock ----------
    /**
     * Reduce el stock en quantity; valida argumentos y actualiza enabled.
     */
    public void reduceStock(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be >= 0");
        if (quantity > this.stock) throw new IllegalArgumentException("Not enough stock to reduce");
        this.stock -= quantity;
        updateEnabledFlag();
    }

    /**
     * Aumenta el stock (reabastecimiento).
     */
    public void increaseStock(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be >= 0");
        this.stock += quantity;
        updateEnabledFlag();
    }

    /**
     * Actualiza la bandera enabled según stock.
     */
    private void updateEnabledFlag() {
        this.enabled = this.stock > 0;
    }

    // ---------- Consultas rápidas para UI ----------
    /**
     * ¿Stock bajo? (usa minStock para decidir).
     */
    public boolean isLowStock() {
        return this.stock <= this.minStock;
    }

    /**
     * ¿Stock crítico? (50% del minStock como ejemplo, o puedes ajustar)
     */
    public boolean isCriticalStock() {
        return this.stock <= Math.max(5, this.minStock / 2);
    }

    public boolean isAvailable() {
        return this.enabled && this.stock > 0;
    }

    // ---------- Validación antes de persistir ----------
    /**
     * Valida campos obligatorios y restricciones básicas.
     * Lanza IllegalArgumentException si hay error.
     */
    public void validateForSave() {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío");
        if (price < 0) throw new IllegalArgumentException("Price debe ser >= 0");
        if (stock < 0) throw new IllegalArgumentException("Stock debe ser >= 0");
        if (cost < 0) throw new IllegalArgumentException("Cost debe ser >= 0");
        if (category == null || category.trim().isEmpty()) throw new IllegalArgumentException("La categoría no puede estar vacía");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", stock=" + stock +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
