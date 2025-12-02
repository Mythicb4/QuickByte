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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferenciasDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();

    public PreferenciasDao(String studentEmail) {
        String safeFileName = studentEmail
                .toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");

        String fileName = safeFileName + ".json";
        this.filePath = Paths.get("data", "preferencias_estudiante", fileName);
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
            throw new RuntimeException("Fallo al inicializar Preferencias DAO", e);
        }
    }

    public List<Product> loadAll() {
        synchronized (lock) {
            if (!Files.exists(filePath))
                return Collections.emptyList();
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<Product>>() {
                }.getType();
                List<Product> list = gson.fromJson(reader, listType);
                return list != null ? list : new ArrayList<>();
            } catch (Exception e) {
                System.err.println("Error cargando preferencias: " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    private void saveAll(List<Product> list) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(list, writer);
            } catch (Exception e) {
                throw new RuntimeException("Fallo guardando preferencias: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Añade una preferencia si no existe (por nombre).
     */
    public void addPreference(Product p) {
        synchronized (lock) {
            List<Product> all = loadAll();
            boolean exists = all.stream()
                    .anyMatch(x -> x.getName() != null && x.getName().equalsIgnoreCase(p.getName()));
            if (!exists) {
                all.add(p);
                saveAll(all);
            }
        }
    }

    public void removePreferenceByName(String name) {
        synchronized (lock) {
            List<Product> all = loadAll();
            boolean removed = all.removeIf(p -> p.getName() != null && p.getName().equalsIgnoreCase(name));
            if (removed)
                saveAll(all);
        }
    }
}
