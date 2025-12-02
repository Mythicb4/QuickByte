package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.StudentPurchase;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DAO para la gestión de Compras de Estudiantes.
 * Guarda en data/historial_estudiante/compras_[email_sanitizado].json
 */
public class StudentPurchaseDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public StudentPurchaseDao(String studentEmail) {
        // 1. Sanitizar el email para usarlo como nombre de archivo
        String safeFileName = studentEmail.toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");
        String fileName = "compras_" + safeFileName + ".json";

        // 2. Ruta: data/historial_estudiante/compras_email.json
        this.filePath = Paths.get("data", "historial_estudiante", fileName);

        // 3. Inicialización de GSON con adaptadores para LocalDateTime
        var localDateTimeSerializer = (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc,
                context) -> new com.google.gson.JsonPrimitive(src.format(DATE_TIME_FORMATTER));

        var localDateTimeDeserializer = (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT,
                context) -> LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                .setPrettyPrinting()
                .create();

        // 4. Inicializar archivo si no existe
        try {
            Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) {
                Files.writeString(filePath, "[]");
            }
        } catch (IOException e) {
            System.err.println("Error inicializando archivo JSON de compras de estudiante: " + e.getMessage());
        }
    }

    /**
     * Cargar todas las compras del estudiante
     */
    public List<StudentPurchase> loadAll() {
        synchronized (lock) {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<StudentPurchase>>() {
                }.getType();
                List<StudentPurchase> purchases = gson.fromJson(reader, listType);
                return purchases != null ? purchases : new ArrayList<>();
            } catch (Exception e) {
                System.err.println("Error leyendo archivo de compras: " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    /**
     * Guardar todas las compras
     */
    private void saveAll(List<StudentPurchase> purchases) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(purchases, writer);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando compras: " + e.getMessage());
            }
        }
    }

    /**
     * Generar ID único para la compra
     */
    public String generateId() {
        return "PUR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Agregar una nueva compra
     */
    public void addPurchase(StudentPurchase purchase) throws Exception {
        synchronized (lock) {
            if (purchase.getId() == null || purchase.getId().isEmpty()) {
                purchase.setId(generateId());
            }

            List<StudentPurchase> all = loadAll();
            all.add(purchase);
            saveAll(all);
        }
    }

    /**
     * Obtener compras por rango de fechas
     * 
     * @param startDate Fecha inicial (inclusive)
     * @param endDate   Fecha final (inclusive)
     * @return Lista de compras dentro del rango
     */
    public List<StudentPurchase> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<StudentPurchase> all = loadAll();

        return all.stream()
                .filter(p -> {
                    LocalDate purchaseDate = p.getPurchaseDate().toLocalDate();
                    return !purchaseDate.isBefore(startDate) && !purchaseDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcular el total gastado en un rango de fechas
     */
    public double getTotalSpentInRange(LocalDate startDate, LocalDate endDate) {
        return getPurchasesByDateRange(startDate, endDate).stream()
                .mapToDouble(StudentPurchase::getTotalAmount)
                .sum();
    }
}
