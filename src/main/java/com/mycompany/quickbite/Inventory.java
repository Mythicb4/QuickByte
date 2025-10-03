package com.mycompany.quickbite;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private final Map<String, ProductVM> byName = new HashMap<>();

    public Inventory() {
    }

    /**
     * Añade un producto nuevo o actualiza uno existente.
     */
    public void addOrUpdate(String name, double price, int quantity) {
        ProductVM product = byName.get(name);
        if (product == null) {
            byName.put(name, new ProductVM(name, price, quantity));
        } else {
            product.setPrice(price);
            product.setQuantity(quantity);
        }
    }

    /**
     * Elimina un producto por nombre.
     * @return true si existía y fue eliminado, false si no existía.
     */
    public boolean remove(String name) {
        return byName.remove(name) != null;
    }

    /**
     * Devuelve todos los productos como una colección.
     */
    public Collection<ProductVM> all() {
        return byName.values();
    }

    /**
     * Obtiene un producto por nombre.
     */
    public ProductVM get(String name) {
        return byName.get(name);
    }
}
