package com.mycompany.quickbite.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase utilitaria para manejar la navegación entre vistas FXML
 * dentro de la aplicación QuickBite.
 */
public class Navigator {

    /**
     * Cambia la vista actual a la indicada por el path del FXML.
     *
     * @param fxmlPath Ruta relativa del archivo FXML (por ejemplo "/views/login.fxml")
     * @param event    Evento que dispara la acción (para obtener el Stage actual)
     */
    public static void navigateTo(String fxmlPath, String name, ActionEvent event) {
        try {
            // Cargar el archivo FXML especificado
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cambiar la escena
            stage.setScene(new Scene(root));
            stage.setTitle("QuickByte - " + name);
            stage.show();

            System.out.println("✅ Navegación exitosa a: " + fxmlPath);
        } catch (IOException e) {
            
            System.err.println("❌ Error al navegar a " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

