package com.mycompany.quickbite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import static javafx.application.Application.launch;
import com.mycompany.quickbite.util.AppState;

public class QuickBiteFX extends Application {

    // Paso 1: Campo estático para almacenar el Stage principal
    private static Stage mainStage;
    // Modo de desarrollo: si es true, se saltará el login y se abrirá
    // directamente la vista de estudiante con credenciales provisionales.
    // Cambia a false antes de mergear o eliminar cuando no se necesite.
    private static final boolean DEV_BYPASS_LOGIN = true;

    @Override
    public void start(Stage stage) throws Exception {
        // Paso 2: Asignar el Stage principal
        mainStage = stage;

        URL fxmlUrl;
        String stageTitle = "QuickByte - Login";

        if (DEV_BYPASS_LOGIN) {
            // Set provisional student session
            AppState.setUserType("estudiante");
            AppState.setUserEmail("user@elpoli.edu.co");

            fxmlUrl = getClass().getResource("/views/login_estudiante.fxml");
            stageTitle = "QuickByte - Dashboard Estudiante (dev)";
            System.out.println("DEV_BYPASS_LOGIN active: starting estudiante view for user@elpoli.edu.co");
        } else {
            fxmlUrl = getClass().getResource("/views/login.fxml");
        }

        System.out.println("Resolving resource -> " + fxmlUrl);
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML no encontrado. Verifica los archivos FXML en src/main/resources/views/");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(stageTitle);
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
