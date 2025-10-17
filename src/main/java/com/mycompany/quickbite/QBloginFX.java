package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class QBloginFX {

    /* ==========================
       REFERENCIAS A ELEMENTOS FXML
       Asegúrate de que todos estos fx:id
       coincidan exactamente con los definidos
       en tu archivo login.fxml
       ========================== */

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnSingup;

    @FXML
    private Label lblForgot;

    @FXML
    private Label lblLogin;

    @FXML
    private Hyperlink linkForgot;

    @FXML
    private ImageView logoImage;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private ImageView userImage;

    @FXML
    private VBox vbLogin;

    @FXML
    private ImageView visibilityImage;

    // Evento del botón "Entrar"
    @FXML
    private void onBtnEntrar(ActionEvent event) {
        System.out.println("Entrar");
    }

    // Evento del botón "Singup"
    @FXML
    private void onBtnSingup(ActionEvent event) {
        Navigator.navigateTo("/views/type.fxml", "type", event);
    }

    // Evento del Hyperlink "Recuperar contraseña"
    @FXML
    private void onLinkForgot(ActionEvent event) {
        System.out.println("Recuperar contraseña");
    }
}
