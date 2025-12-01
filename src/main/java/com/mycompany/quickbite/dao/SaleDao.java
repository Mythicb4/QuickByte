package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // ⬅️ NUEVA IMPORTACIÓN
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Sale;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException; // ⬅️ Agregada para consistencia en el manejo de excepciones
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime; // ⬅️ NECESARIO
import java.time.format.DateTimeFormatter; // ⬅️ NECESARIO
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// GSON TypeAdapter imports (implícitos si usas Java 8 lambdas y el código a continuación)
// import com.google.gson.JsonSerializer;
// import com.google.gson.JsonDeserializer;
// import com.google.gson.JsonPrimitive;


/**
 * DAO para la gestión de Ventas, usa el email del negocio para el nombre del archivo JSON (Rotación).
 */
public class SaleDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();
    
    // Formato estándar para guardar la fecha en el JSON.
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 

    public SaleDao(String businessEmail) {
        // 1. Sanitizar el email para usarlo como nombre de archivo
        String safeFileName = businessEmail.toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");
        String fileName = "ventas_" + safeFileName + ".json";

        // 2. Ruta: data/ventas_negocio/ventas_email.json
        this.filePath = Paths.get("data", "ventas_negocio", fileName);
        
        // 3. 🔑 CORRECCIÓN: Inicialización de GSON con adaptadores para LocalDateTime 🔑
        // Serializador: Convierte LocalDateTime a String
        var localDateTimeSerializer = (com.google.gson.JsonSerializer<LocalDateTime>) 
            (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(DATE_TIME_FORMATTER));

        // Deserializador: Convierte String a LocalDateTime
        var localDateTimeDeserializer = (com.google.gson.JsonDeserializer<LocalDateTime>) 
            (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);

        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
            .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
            .setPrettyPrinting() // Hace que el JSON sea más legible
            .create();
        // -------------------------------------------------------------------------
        
        // 4. Inicializar archivo si no existe
        try {
            Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) {
                Files.writeString(filePath, "[]");
            }
        } catch (IOException e) {
            System.err.println("Error inicializando archivo JSON de ventas: " + e.getMessage());
        }
    }

    public List<Sale> loadAll() { // Usa Sale
        synchronized (lock) {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<Sale>>() {}.getType();
                List<Sale> sales = gson.fromJson(reader, listType);
                return sales != null ? sales : new ArrayList<>();
            } catch (Exception e) {
                // El error de GSON aparecía aquí al cargar
                System.err.println("Error leyendo archivo de ventas: " + e.getMessage());
                return new ArrayList<>(); 
            }
        }
    }

    private void saveAll(List<Sale> ventas) { // Usa Sale
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                // La serialización ocurre aquí
                gson.toJson(ventas, writer);
            } catch (Exception e) {
                // El error de GSON aparecía aquí al guardar
                throw new RuntimeException("Error guardando ventas: " + e.getMessage());
            }
        }
    }

    public String generateId() {
        List<Sale> all = loadAll();
        int maxId = all.stream()
                .map(Sale::getId)
                .filter(id -> id != null && id.startsWith("VEN-"))
                .map(id -> id.substring(4))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return String.format("VEN-%03d", maxId + 1);
    }

    public void addSale(Sale sale) throws Exception { 
        synchronized (lock) {
            if (sale.getId() == null || sale.getId().isEmpty()) {
                 sale.setId(generateId());
            }
            // Asumiendo que el campo de fecha se inicializa aquí o en el Service/Model.
            
            List<Sale> all = loadAll();
            all.add(sale);
            saveAll(all);
        }
    }
}