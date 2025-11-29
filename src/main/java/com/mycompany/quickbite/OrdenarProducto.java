
package com.mycompany.quickbite;

/**
 * Representa un ítem que se añade al pedido/carrito.
 */
public class OrdenarProducto {
    private final String nombre;
    private final double precio;
    private final int cantidad;
    private String producto;

    public OrdenarProducto(String nombre, String producto, double precio, int cantidad) {
        this.nombre = nombre;
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters
    public String getName(){
        return nombre;
    }
    public String getProductName() {
        return producto;
    }

    public double getPrice() {
        return precio;
    }

    public int getQuantity() {
        return cantidad;
    }
    
    public double getSubtotal(){
        return precio* cantidad;
    }

    // Método para imprimir la orden (ejemplo)
    @Override
    public String toString() {
        return cantidad+ " x " + nombre + " @ $" + precio + " (Total: $" + (precio * cantidad) + ")";
    }
}