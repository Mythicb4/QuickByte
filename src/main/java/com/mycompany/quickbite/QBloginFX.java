package com.mycompany.quickbite;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
// Si quieres ocultar la contraseña, reemplaza TextField por PasswordField y descomenta la línea
// import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class QBloginFX extends Application {
    private static HBox header(String title) {
        Text t = new Text(title);
        HBox box = new HBox(t);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        root.getStyleClass().add("fondo");
        root.setPrefSize(800, 600);
        Scene scene = new Scene(root, 800, 600);

        // Cargar CSS desde la raíz del classpath (src/main/resources/QBlogin.css)
        java.net.URL cssUrl = QBloginFX.class.getResource("/QBlogin.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: QBlogin.css no encontrado en src/main/resources");
        }

        stage.setScene(scene);
        stage.setTitle("QuickBite");
        Navigator.init(stage); // Asegúrate de que Navigator esté en el paquete com.mycompany.quickbyte
        stage.show();

        HBox headerBox = header("QuickBite");
        headerBox.getStyleClass().add("headerbox");

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre");

        TextField passwordField = new TextField(); // <-- Si quieres ocultar la contraseña, usa PasswordField
        // PasswordField passwordField = new PasswordField();

        passwordField.setPromptText("Contraseña");
        Button loginBtn = new Button("Login");
        VBox form = new VBox(8, nameField, passwordField, loginBtn);
        form.getStyleClass().add("form");

        // Al hacer login → navegar a inventario
        loginBtn.setOnAction(e -> Navigator.showInventory());

        headerBox.setLayoutX((800 / 2) - 200);
        headerBox.setLayoutY(0);
        headerBox.setPrefWidth(400);
        headerBox.setPrefHeight(100);

        form.getChildren().setAll(nameField, passwordField, loginBtn);
        form.setSpacing(12);

        form.setLayoutX((800 / 2) - 100);
        form.setLayoutY(200);
        form.setPrefWidth(200);
        form.setPrefHeight(220);
        root.getChildren().addAll(headerBox, form);
    }

    public static void main(String[] args) {
        Application.launch(QBloginFX.class, args);
    }
}
