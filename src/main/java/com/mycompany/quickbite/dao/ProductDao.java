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

    public ProductDao(String businessEmail) {
        // 1. Sanitizar el email para usarlo como nombre de archivo
        // Reemplazamos caracteres especiales por si el sistema de archivos los rechaza.
        String safeFileName = businessEmail
                .toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");
        
        String fileName = safeFileName + ".json";
        
        // 2. Definir la ruta: data/productos_negocio/email_at_negocio.json
        this.filePath = Paths.get("data", "productos_negocio", fileName);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        ensureDataFileExists();
    }
    
    // *** ensureDataFileExists() CORREGIDO PARA CREAR CARPETAS ***
    private void ensureDataFileExists() {
        try {
            Path parent = filePath.getParent();
            // Asegura que la carpeta 'data/productos_negocio' exista
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            
            // Crea el archivo JSON si no existe
            if (!Files.exists(filePath)) {
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    // Escribe un array JSON vacío [] para inicializar
                    writer.write("[]");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al asegurar la existencia del archivo de datos: " + e.getMessage());
            // Dependiendo de tu manejo de errores, podrías relanzar o ignorar.
            throw new RuntimeException("Fallo al inicializar el DAO.", e);
        }
    }

    // ProductDao.java
// ...

    public List<Product> loadAll() {
        synchronized (lock) {
            if (!Files.exists(filePath)) {
                return Collections.emptyList();
            }
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type productListType = new TypeToken<List<Product>>() {}.getType();
                List<Product> products = gson.fromJson(reader, productListType);
                return products != null ? products : Collections.emptyList();
            } catch (Exception e) {
                // Si la carga falla (ej: archivo corrupto), movemos el archivo
                // para evitar que se sobrescriba con una lista vacía en el próximo guardado.
                System.err.println("Error al cargar productos. El archivo puede estar corrupto: " + e.getMessage());
                try {
                    Path corruptedPath = Paths.get(filePath.toString() + ".corrupted." + System.currentTimeMillis());
                    Files.move(filePath, corruptedPath);
                    System.err.println("Archivo corrupto movido a: " + corruptedPath);
                } catch (Exception moveE) {
                    System.err.println("Fallo al mover el archivo corrupto: " + moveE.getMessage());
                }
                // Se devuelve una lista vacía, y la app continuará como si no hubiera datos.
                return Collections.emptyList();
            }
        }
    }

    private void saveAll(List<Product> products) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(products, writer);
            } catch (Exception e) {
                System.err.println("Error al guardar productos: " + e.getMessage());
                // Propagamos la excepción al servicio/controlador
                throw new RuntimeException("Fallo la persistencia en el archivo de datos: " + e.getMessage(), e);
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
