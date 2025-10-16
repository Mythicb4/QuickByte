package com.mycompany.quickbite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class QuickBiteFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Usa ruta absoluta desde resources
        URL fxmlUrl = getClass().getResource("/views/login.fxml");
        System.out.println("Resolving resource /views/login.fxml -> " + fxmlUrl);
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML no encontrado: /views/login.fxml. Verifica src/main/resources/views/login.fxml");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("QuickByte - Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
