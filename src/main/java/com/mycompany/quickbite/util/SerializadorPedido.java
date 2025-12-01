package com.mycompany.quickbite.util;

import com.mycompany.quickbite.OrdenarProducto;
import java.util.List;

public class SerializadorPedido {

    /**
     * Convierte los ítems del carrito en un String compacto para el QR.
     * Formato: Usuario:EMAIL|Producto:NOMBRE|Precio:X|Cant:Y...
     * 
     * @return El String serializado.
     */
    public static String serializar() {
        // 1. Obtener la lista de ítems del carrito
        List<OrdenarProducto> pedido = CarritoManager.getInstancia().getItems();

        // 2. Obtener el usuario logueado
        if (pedido.isEmpty()) {
            return null;
        }

        String currentUser = AppState.getUserEmail();
        // Si AppState no tiene usuario, intentar obtenerlo del primer ítem del pedido
        if (currentUser == null && !pedido.isEmpty()) {
            currentUser = pedido.get(0).getUserEmail();
        }
        // Si aún así no hay usuario, usar un marcador genérico
        if (currentUser == null) {
            currentUser = "unknown";
        }

        // 3. Iniciar la serialización con el usuario
        StringBuilder qrContent = new StringBuilder("Usuario:").append(currentUser);

        // 4. Serializar cada ítem
        for (OrdenarProducto item : pedido) {
            qrContent.append("|Producto:")
                    .append(item.getProductName())
                    .append("|Precio:")
                    // Usamos String.format para asegurar que los decimales sean consistentes
                    .append(String.format("%.2f", item.getPrice()))
                    .append("|Cant:")
                    .append(item.getQuantity());
        }

        return qrContent.toString();
    }
}