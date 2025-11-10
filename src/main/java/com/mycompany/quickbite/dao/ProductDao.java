package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Product;

import java.io.FileWriter;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO simple para persistencia en JSON (data/products.json).
 * Operaciones: loadAll, saveAll, findById, add, update, disable, remove, generateId.
 */
public class ProductDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();

    public ProductDao(String filePathStr) {
        this.filePath = Paths.get(filePathStr);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ensureDataFileExists();
    }

    private void ensureDataFileExists() {
        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(filePath)) {
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    writer.write("[]");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el archivo de datos: " + filePath, e);
        }
    }

    public List<Product> loadAll() {
        synchronized (lock) {
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<Product>>() {}.getType();
                List<Product> list = gson.fromJson(reader, listType);
                if (list == null) return new ArrayList<>();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public void saveAll(List<Product> products) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(products, writer);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando productos en " + filePath, e);
            }
        }
    }

    public Optional<Product> findById(String id) {
        if (id == null) return Optional.empty();
        return loadAll().stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
    }

    public String generateId() {
        List<Product> all = loadAll();
        List<Integer> nums = all.stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .filter(s -> s.startsWith("PRD-"))
                .map(s -> s.substring(4))
                .filter(s -> s.matches("\\d+"))
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        int next = 1;
        if (!nums.isEmpty()) {
            next = nums.get(nums.size() - 1) + 1;
        }
        return String.format("PRD-%03d", next);
    }

    public Product addProduct(Product product) {
        synchronized (lock) {
            Objects.requireNonNull(product, "product must not be null");
            if (product.getId() == null || product.getId().trim().isEmpty()) {
                product.setId(generateId());
            } else {
                if (findById(product.getId()).isPresent()) {
                    throw new IllegalArgumentException("Product id already exists: " + product.getId());
                }
            }

            product.validateForSave();

            List<Product> all = loadAll();
            all.add(product);
            saveAll(all);
            return product;
        }
    }

    public Product updateProduct(Product updated) {
        Objects.requireNonNull(updated, "updated product must not be null");
        if (updated.getId() == null) throw new IllegalArgumentException("Product id is required for update");

        synchronized (lock) {
            List<Product> all = loadAll();
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                if (Objects.equals(all.get(i).getId(), updated.getId())) {
                    updated.validateForSave();
                    all.set(i, updated);
                    found = true;
                    break;
                }
            }
            if (!found) throw new NoSuchElementException("Product not found: " + updated.getId());
            saveAll(all);
            return updated;
        }
    }

    public Product disableProduct(String id) {
        Objects.requireNonNull(id, "id must not be null");
        synchronized (lock) {
            List<Product> all = loadAll();
            for (Product p : all) {
                if (id.equals(p.getId())) {
                    // ponemos stock a 0 para forzar enabled=false vía setStock
                    p.setStock(0);
                    saveAll(all);
                    return p;
                }
            }
            throw new NoSuchElementException("Product not found: " + id);
        }
    }

    public void removeProduct(String id) {
        Objects.requireNonNull(id, "id must not be null");
        synchronized (lock) {
            List<Product> all = loadAll();
            boolean removed = all.removeIf(p -> id.equals(p.getId()));
            if (!removed) throw new NoSuchElementException("Product not found: " + id);
            saveAll(all);
        }
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public List<Product> findByCategory(String category) {
        if (category == null) return Collections.emptyList();
        return loadAll().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .collect(Collectors.toList());
    }
}
