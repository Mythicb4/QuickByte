package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Client;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ClientDao {

    private final Path filePath;
    private final Gson gson;
    private final Object lock = new Object();

    // Constructor que recibe el email del negocio (desde SessionContext)
    public ClientDao(String businessEmail) {
        // 1. Crear nombre de archivo basado en el email
        String safeFileName = businessEmail.toLowerCase()
                .replace("@", "_at_")
                .replace(".", "_dot_");
        String fileName = "clients_" + safeFileName + ".json";

        // 2. Ruta: data/clientes_negocio/clients_email.json
        this.filePath = Paths.get("data", "clientes_negocio", fileName);
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
            throw new RuntimeException("Error inicializando archivo de clientes: " + e.getMessage(), e);
        }
    }

    public List<Client> loadAll() {
        synchronized (lock) {
            if (!Files.exists(filePath)) return new ArrayList<>();
            try (FileReader reader = new FileReader(filePath.toFile())) {
                Type listType = new TypeToken<List<Client>>() {}.getType();
                List<Client> list = gson.fromJson(reader, listType);
                return list != null ? list : new ArrayList<>();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }

    private void saveAll(List<Client> clients) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(clients, writer);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando clientes: " + e.getMessage());
            }
        }
    }

    public String generateId() {
        List<Client> all = loadAll();
        int maxId = all.stream()
                .map(Client::getId)
                .filter(id -> id != null && id.startsWith("CLI-"))
                .map(id -> id.substring(4))
                .filter(num -> num.matches("\\d+")) // Asegura que sea numérico
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return String.format("CLI-%03d", maxId + 1);
    }

    public void addClient(Client client) throws Exception {
        synchronized (lock) {
            List<Client> all = loadAll();
            
            // Validaciones (RF: Unicidad por documento/cédula)
            if (all.stream().anyMatch(c -> c.getCedula().equals(client.getCedula()))) {
                throw new Exception("Ya existe un cliente con la cédula: " + client.getCedula());
            }

            if (client.getId() == null || client.getId().isEmpty()) {
                client.setId(generateId());
            }
            
            all.add(client);
            saveAll(all);
        }
    }

    public void updateClient(Client updated) throws Exception {
        synchronized (lock) {
            List<Client> all = loadAll();
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(updated.getId())) {
                    // Validar que no duplique cédula de otro
                    String newCedula = updated.getCedula();
                    String currentId = updated.getId();
                    boolean cedulaDuplicated = all.stream()
                            .anyMatch(c -> !c.getId().equals(currentId) && c.getCedula().equals(newCedula));
                            
                    if (cedulaDuplicated) throw new Exception("La cédula ya pertenece a otro cliente.");

                    all.set(i, updated);
                    found = true;
                    break;
                }
            }
            if (!found) throw new Exception("Cliente no encontrado.");
            saveAll(all);
        }
    }

    public void removeClient(String id) throws Exception {
        synchronized (lock) {
            List<Client> all = loadAll();
            boolean removed = all.removeIf(c -> c.getId().equals(id));
            if (!removed) throw new Exception("No se pudo eliminar: Cliente no encontrado.");
            saveAll(all);
        }
    }

    // Buscador por Nombre o Cédula
    public List<Client> search(String query) {
        if (query == null || query.trim().isEmpty()) return loadAll();
        String q = query.toLowerCase();
        return loadAll().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(q) || c.getCedula().contains(q))
                .collect(Collectors.toList());
    }
}