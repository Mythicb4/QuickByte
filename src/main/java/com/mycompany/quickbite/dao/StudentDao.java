package com.mycompany.quickbite.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quickbite.model.Student;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private static final Path STUDENTS_FILE = Path.of("data", "students.json");
    private final Gson gson = new Gson();

    // Crea el archivo y carpeta si no existen
    public StudentDao() {
        try {
            Files.createDirectories(STUDENTS_FILE.getParent());
            if (Files.notExists(STUDENTS_FILE)) {
                Files.writeString(STUDENTS_FILE, "[]");
            }
        } catch (IOException e) {
            System.err.println("Error inicializando archivo JSON: " + e.getMessage());
        }
    }

    // Leer todos los estudiantes
    public List<Student> getAllStudents() {
        try (FileReader reader = new FileReader(STUDENTS_FILE.toFile())) {
            Type listType = new TypeToken<List<Student>>() {}.getType();
            List<Student> students = gson.fromJson(reader, listType);
            return (students != null) ? students : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Guardar un nuevo estudiante
    public void addStudent(Student student) {
        List<Student> students = getAllStudents();
        students.add(student);
        saveAll(students);
    }

    // Guardar toda la lista
    private void saveAll(List<Student> students) {
        try (FileWriter writer = new FileWriter(STUDENTS_FILE.toFile())) {
            gson.toJson(students, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verificar si un email ya está registrado
    public boolean emailExists(String email) {
        return getAllStudents().stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(email));
    }
    
    public boolean validateCredentials(String email, String password) {
        return getAllStudents().stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(email)
                        && s.getPassword().equals(password));
    }
}
