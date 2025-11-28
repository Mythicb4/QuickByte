package com.mycompany.quickbite.util;

import com.mycompany.quickbite.QuickBiteFX;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.util.HashSet;
import java.util.Set;
import java.net.URL; // Asegúrate de tener esta importación

public class Navigator {

    // Conjunto para rastrear todas las ventanas flotantes (secundarias) abiertas
    private static final Set<Stage> floatingStages = new HashSet<>(); 

    /**
     * Cierra todas las ventanas secundarias rastreadas y limpia el conjunto.
     */
    private static void closeAllFloatingStages() {
        for (Stage stage : floatingStages) {
            if (stage.isShowing()) {
                stage.close();
            }
        }
        floatingStages.clear();
    }

    /**
     * Cierra la ventana (Stage) que originó el ActionEvent.
     * Útil para botones "Cancelar" o "Cerrar" en ventanas flotantes.
     * * @param event El ActionEvent que disparó el cierre (ej: el click en un botón).
     */
    public static void closeStage(ActionEvent event) {
        try {
            // Obtener el Stage a partir del elemento que disparó el evento (Node)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Cerrar la Stage
            stage.close();
            
            // Remover de la lista de stages flotantes (si estaba allí).
            // Esto es una medida de seguridad, ya que el listener onHidden debería haberlo hecho.
            floatingStages.remove(stage);

        } catch (Exception e) {
            System.err.println("Error al cerrar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void navigateTo(String fxmlPath, String name, boolean replaceCurrentStage, ActionEvent event) {
        try {
            URL fxmlUrl = Navigator.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                 throw new RuntimeException("FXML no encontrado: " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Obtenemos el Stage actual donde ocurrió el evento
            Stage eventStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (replaceCurrentStage) {
                // CASO TRUE: Reemplazar contenido en el Stage Principal y limpiar flotantes.
                closeAllFloatingStages(); // Cierra todas las flotantes ANTES de cambiar la vista

                Stage mainStage = QuickBiteFX.getMainStage();

                mainStage.setScene(new Scene(root));
                mainStage.setTitle("QuickByte - " + name);
                mainStage.show();

                if (eventStage != mainStage) {
                    eventStage.close();
                }

            } else {
                // CASO FALSE: Abrir una nueva ventana flotante (secundaria).
                
                // 🔑 SOLUCIÓN: Cierra todas las ventanas flotantes existentes
                // antes de abrir la nueva. Esto asegura que solo haya una ventana 
                // de "Ventas" abierta a la vez (siempre que se use navigateTo).
                closeAllFloatingStages(); 

                // 1. Crear un Stage nuevo
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("QuickByte - " + name);

                // 2. Agregar a la lista de seguimiento
                floatingStages.add(newStage);
                
                // Remover de la lista cuando la ventana se cierra
                newStage.setOnHidden(e -> floatingStages.remove(newStage));

                // 3. Mostrar la nueva ventana
                newStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}