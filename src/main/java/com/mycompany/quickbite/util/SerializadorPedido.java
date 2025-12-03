package com.mycompany.quickbite.util;

import com.mycompany.quickbite.OrdenarProducto;
import java.util.List;

public class SerializadorPedido {

    /**
     * Convierte los ítems del carrito en un String ultra compacto para el QR.
     * Formato: U:email|B:negocio|T:total|I:nombre,cant,precio;nombre2,cant2,precio2
     * 
     * @return El String serializado minimalista.
     */
    public static String serializar() {
        List<OrdenarProducto> pedido = CarritoManager.getInstancia().getItems();
        if (pedido.isEmpty()) {
            return null;
        }

        // Obtener datos básicos
        String user = AppState.getUserEmail();
        if (user == null && !pedido.isEmpty()) {
            user = pedido.get(0).getUserEmail();
        }
        if (user == null) {
            user = "unknown";
        }

        String business = "";
        if (AppState.getSelectedBusiness() != null) {
            business = AppState.getSelectedBusiness().getEmail();
        }

        // Calcular total y construir string compacto
        StringBuilder sb = new StringBuilder();
        sb.append("U:").append(user);
        sb.append("|B:").append(business);

        double total = 0.0;
        sb.append("|I:");

        for (int i = 0; i < pedido.size(); i++) {
            OrdenarProducto item = pedido.get(i);
            if (i > 0)
                sb.append(";");

            // nombre,cantidad,precio
            sb.append(item.getProductName()).append(",")
                    .append(item.getQuantity()).append(",")
                    .append(String.format("%.0f", item.getPrice()));

            total += item.getPrice() * item.getQuantity();
        }

        sb.append("|T:").append(String.format("%.0f", total));

        return sb.toString();
    }
}