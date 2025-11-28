package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Venta;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para la gestión de Ventas, usa el email del negocio para el nombre del archivo JSON (Rotación).
 */
public class VentaDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();

    public VentaDao(String businessEmail) {
        // 1. Sanitizar el email para usarlo como nombre de archivo
        String safeFileName = businessEmail.toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");
        String fileName = "ventas_" + safeFileName + ".json";

        // 2. Ruta: data/ventas_negocio/ventas_email.json
        this.filePath = Paths.get("data", "ventas_negocio", fileName);
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
                    writer.write("[]"); // Inicializa con lista vacía
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando archivo de ventas: " + e.getMessage(), e);
        }
    }

    public List<Venta> loadAll() {
        synchronized (lock) {
            if (!Files.exists(filePath)) return new ArrayList<>();
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<Venta>>() {}.getType();
                List<Venta> list = gson.fromJson(reader, listType);
                return list != null ? list : new ArrayList<>();
            } catch (Exception e) {
                System.err.println("Error leyendo archivo de ventas: " + e.getMessage());
                // Si hay un error de parseo (JSON corrupto), devuelve una lista vacía
                return new ArrayList<>(); 
            }
        }
    }

    private void saveAll(List<Venta> ventas) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(ventas, writer);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando ventas: " + e.getMessage());
            }
        }
    }

    public String generateId() {
        List<Venta> all = loadAll();
        int maxId = all.stream()
                .map(Venta::getId)
                .filter(id -> id != null && id.startsWith("VEN-"))
                .map(id -> id.substring(4))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return String.format("VEN-%03d", maxId + 1);
    }

    public void addVenta(Venta venta) throws Exception {
        synchronized (lock) {
            if (venta.getId() == null || venta.getId().isEmpty()) {
                 venta.setId(generateId());
            }
            List<Venta> all = loadAll();
            all.add(venta);
            saveAll(all);
        }
    }
}