
package com.mycompany.quickbite;

/**
 * Representa un ítem que se añade al pedido/carrito.
 */
public class OrdenarProducto {
    private final String userEmail; // Usuario que hizo el pedido
    private final String productId; // ID del producto
    private final String productName; // Nombre del producto
    private final double price;
    private int quantity; // mutable para permitir combinar items

    public OrdenarProducto(String userEmail, String productId, String productName, double price, int quantity) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getUserEmail() {
        return userEmail;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return price * quantity;
    }

    // Permite aumentar la cantidad cuando se agrega el mismo producto
    public void increaseQuantity(int delta) {
        if (delta > 0) {
            this.quantity += delta;
        }
    }

    @Override
    public String toString() {
        return quantity + " x " + productName + " @ $" + price + " (Total: $" + (price * quantity) + ")";
    }
}