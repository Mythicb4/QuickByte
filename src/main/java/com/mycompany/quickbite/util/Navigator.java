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

public class Navigator {

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

    public static void navigateTo(String fxmlPath, String name, boolean replaceCurrentStage, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Parent root = loader.load();

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
                // de "Ventas" abierta a la vez.
                closeAllFloatingStages(); 

                // 1. Crear un Stage nuevo
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("QuickByte - " + name);

                // 2. Agregar a la lista de seguimiento
                floatingStages.add(newStage);
                
                newStage.setOnHidden(e -> floatingStages.remove(newStage));

                // 3. Mostrar la nueva ventana
                newStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}