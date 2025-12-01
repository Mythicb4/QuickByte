/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickbite.util;

/**
 *
 * @author USUARIO CAB
 */

import com.mycompany.quickbite.OrdenarProducto; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class CarritoManager {

    // Única instancia de la clase (Singleton)
    private static CarritoManager instancia;
    
    // Lista observable para almacenar y mostrar los ítems del carrito
    private final ObservableList<OrdenarProducto> items;

    // Constructor privado para el Singleton
    private CarritoManager() {
        items = FXCollections.observableArrayList();
    }

    // Método para obtener la única instancia
    public static CarritoManager getInstancia() {
        if (instancia == null) {
            instancia = new CarritoManager();
        }
        return instancia;
    }

    // --- MÉTODOS PÚBLICOS ---

    public ObservableList<OrdenarProducto> getItems() {
        return items;
    }

    public void addItem(OrdenarProducto item) {
        // Si ya existe un ítem con el mismo usuario y producto, aumentamos la cantidad
        for (OrdenarProducto existing : items) {
            if (existing.getUserEmail() != null
                    && item.getUserEmail() != null
                    && existing.getUserEmail().equals(item.getUserEmail())
                    && existing.getProductName().equalsIgnoreCase(item.getProductName())) {
                existing.increaseQuantity(item.getQuantity());
                return;
            }
        }

        // Si no existe, lo añadimos
        items.add(item);
    }

    public double calcularTotal() {
        return items.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
    }

    public void vaciarCarrito() {
        items.clear();
    }
}
