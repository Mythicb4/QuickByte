package com.mycompany.quickbite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import static javafx.application.Application.launch;

public class QuickBiteFX extends Application {

    // Paso 1: Campo estático para almacenar el Stage principal
    private static Stage mainStage; 

    @Override
    public void start(Stage stage) throws Exception {
        // Paso 2: Asignar el Stage principal
        mainStage = stage; 
        
        // Usa ruta absoluta desde resources
        URL fxmlUrl = getClass().getResource("/views/login.fxml");
        System.out.println("Resolving resource /views/login_negocio.fxml -> " + fxmlUrl);
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML no encontrado: /views/producto_negocio.fxml. Verifica src/main/resources/views/producto_negocio.fxml");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("QuickByte - Login");
        stage.show();
    }
    
    // Paso 3: Método estático para obtener la referencia al Stage principal
    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
