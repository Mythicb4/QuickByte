package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Business;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BusinessDao {
    private static final Path BUSINESSES_FILE = Path.of("data", "businesses.json");
    private final Gson gson = new Gson();

    public BusinessDao() {
        try {
            Files.createDirectories(BUSINESSES_FILE.getParent());
            if (Files.notExists(BUSINESSES_FILE)) {
                Files.writeString(BUSINESSES_FILE, "[]");
            }
        } catch (IOException e) {
            System.err.println("Error inicializando archivo JSON de negocios: " + e.getMessage());
        }
    }

    public List<Business> getAllBusinesses() {
        try (FileReader reader = new FileReader(BUSINESSES_FILE.toFile())) {
            Type listType = new TypeToken<List<Business>>() {}.getType();
            List<Business> businesses = gson.fromJson(reader, listType);
            return (businesses != null) ? businesses : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addBusiness(Business business) {
        List<Business> businesses = getAllBusinesses();
        businesses.add(business);
        saveAll(businesses);
    }

    private void saveAll(List<Business> businesses) {
        try (FileWriter writer = new FileWriter(BUSINESSES_FILE.toFile())) {
            gson.toJson(businesses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        return getAllBusinesses().stream()
                .anyMatch(b -> b.getEmail().equalsIgnoreCase(email));
    }
    
    public boolean validateCredentials(String email, String password) {
        return getAllBusinesses().stream()
                .anyMatch(b -> b.getEmail().equalsIgnoreCase(email)
                        && b.getPassword().equals(password));
    }
}
