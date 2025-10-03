package com.mycompany.quickbite;

import javafx.stage.Stage;

public final class Navigator {

    private static Stage stage;

    private Navigator() {
        // Constructor privado para evitar instanciación
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void showLogin() {
        if (stage == null) {
            throw new IllegalStateException("Navigator no inicializado. Llama a init(stage) primero.");
        }
        try {
            new QBloginFX().start(stage);
        } catch (Exception e) {
            e.printStackTrace(); // Mostrar el error completo en consola
            throw new RuntimeException("Error al mostrar la pantalla de login", e);
        }
    }

    public static void showInventory() {
        if (stage == null) {
            throw new IllegalStateException("Navigator no inicializado. Llama a init(stage) primero.");
        }
        try {
            new QuickBiteFX().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al mostrar la pantalla de inventario", e);
        }
    }
}
