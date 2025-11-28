package com.mycompany.quickbite.util;

/**
 * Clase estática para mantener el estado global de la sesión.
 */
public class SessionContext {
    
    // Almacena el email del negocio que inició sesión.
    private static String loggedInBusinessEmail;

    /**
     * Establece el email del negocio actual.
     * @param email El email de la cuenta de negocio.
     */
    public static void setLoggedInBusinessEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío.");
        }
        loggedInBusinessEmail = email.trim();
    }
    

    /**
     * Obtiene el email del negocio actual.
     * @return El email del negocio.
     * @throws IllegalStateException si no hay ningún negocio logueado.
     */
    public static String getLoggedInBusinessEmail() {
        if (loggedInBusinessEmail == null) {
            throw new IllegalStateException("No hay un negocio logueado en la sesión.");
        }
        return loggedInBusinessEmail;
    }
    
    /**
     * Cierra la sesión (opcional).
     */
    public static void clearSession() {
        loggedInBusinessEmail = null;
    }
}