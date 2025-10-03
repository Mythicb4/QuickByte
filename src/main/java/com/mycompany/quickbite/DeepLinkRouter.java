package com.mycompany.quickbite;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DeepLinkRouter {

    private final Inventory inventory;
    private Runnable onInventoryViewRequested = () -> {};

    public DeepLinkRouter(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setOnInventoryViewRequested(Runnable callback) {
        this.onInventoryViewRequested = callback != null ? callback : () -> {};
    }

    /**
     * Maneja un deeplink del tipo:
     * quickbite://add?name=...&price=...&qty=...
     * quickbite://update?name=...&price=...&qty=...
     * quickbite://remove?name=...
     * quickbite://open?view=inventory
     */
    public String handle(String url) {
        try {
            URI uri = URI.create(url);
            if (!"quickbite".equalsIgnoreCase(uri.getScheme())) {
                return "Esquema no soportado";
            }

            String action = uri.getHost();
            Map<String, String> params = parseQuery(uri.getQuery());

            if (action == null) return "Acción no especificada";

            switch (action.toLowerCase()) {
                case "open":
                    String view = params.getOrDefault("view", "inventory");
                    if ("inventory".equalsIgnoreCase(view)) {
                        onInventoryViewRequested.run();
                        return "Vista de inventario";
                    } else {
                        return "Vista no reconocida: " + view;
                    }

                case "add":
                case "update":
                    return addOrUpdate(params);

                case "remove":
                    String name = params.get("name");
                    if (name != null && !name.isEmpty()) {
                        boolean removed = inventory.remove(name);
                        return removed ? "Eliminado: " + name : "No encontrado: " + name;
                    }
                    return "Nombre requerido";

                default:
                    return "Acción no soportada: " + action;
            }

        } catch (IllegalArgumentException e) {
            return "URI inválida: " + e.getMessage();
        }
    }

    private String addOrUpdate(Map<String, String> params) {
        String name = params.get("name");
        if (name != null && !name.isEmpty()) {
            double price = parseDouble(params.get("price"), 0.0);
            int qty = parseInt(params.get("qty"), 0);
            inventory.addOrUpdate(name, price, qty);
            return "Guardado: " + name;
        }
        return "Nombre requerido";
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    String key = urlDecode(pair.substring(0, idx));
                    String value = urlDecode(pair.substring(idx + 1));
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }

    private static double parseDouble(String s, double def) {
        if (s != null && !s.isEmpty()) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return def;
            }
        }
        return def;
    }

    private static int parseInt(String s, int def) {
        if (s != null && !s.isEmpty()) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return def;
            }
        }
        return def;
    }
}
