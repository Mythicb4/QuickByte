package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class QBloginFX {

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
    private TextField tfPasswordVisible;

    @FXML
    private TextField txtEmail;

    @FXML
    private ImageView userImage;

    @FXML
    private VBox vbLogin;

    @FXML
    private ImageView visibilityImageOff;

    @FXML
    private ImageView visibilityImageOn;

    // indica si la contraseña está visible (texto plano)
    private boolean passwordVisible = false;

    @FXML
    private void initialize() {
        // 1) sincronizar el texto entre PasswordField y TextField
        if (tfPassword != null && tfPasswordVisible != null) {
            // bidireccional para mantener ambos actualizados automaticamente
            tfPasswordVisible.textProperty().bindBidirectional(tfPassword.textProperty());

            // asegurar estados iniciales correctos
            tfPassword.setVisible(true);
            tfPassword.setManaged(true);

            tfPasswordVisible.setVisible(false);
            tfPasswordVisible.setManaged(false);
        }

        // 2) asegurar estado inicial de iconos (off = ojo cerrado visible; on = ojo abierto oculto)
        if (visibilityImageOff != null && visibilityImageOn != null) {
            visibilityImageOff.setVisible(true);
            visibilityImageOn.setVisible(false);

            // registrar click handlers como respaldo si FXML no los llama
            visibilityImageOff.setOnMouseClicked(e -> togglePasswordVisibility());
            visibilityImageOn.setOnMouseClicked(e -> togglePasswordVisibility());

            // mejorar UX: cambiar cursor a mano desde CSS sería ideal, pero si quieres:
            visibilityImageOff.setStyle("-fx-cursor: hand;");
            visibilityImageOn.setStyle("-fx-cursor: hand;");
        }
    }

    // Método ligado en FXML: onMouseClicked="#onVisibilityClick"
    // También soporta ser llamado desde setOnMouseClicked del initialize
    @FXML
    void onVisibilityClick(MouseEvent event) {
        togglePasswordVisibility();
    }

    // Lógica central para alternar la visibilidad
    void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        // alternar los campos (visible + managed) para que la UI no "salte"
        if (tfPassword != null && tfPasswordVisible != null) {
            tfPassword.setVisible(!passwordVisible);
            tfPassword.setManaged(!passwordVisible);

            tfPasswordVisible.setVisible(passwordVisible);
            tfPasswordVisible.setManaged(passwordVisible);

            // opcional: mover el foco al campo visible para mejor UX
            if (passwordVisible) {
                tfPasswordVisible.requestFocus();
                tfPasswordVisible.positionCaret(tfPasswordVisible.getText() != null ? tfPasswordVisible.getText().length() : 0);
            } else {
                tfPassword.requestFocus();
                tfPassword.positionCaret(tfPassword.getText() != null ? tfPassword.getText().length() : 0);
            }
        }

        // alternar iconos (tú tienes dos ImageView distintos)
        if (visibilityImageOff != null && visibilityImageOn != null) {
            visibilityImageOff.setVisible(!passwordVisible);
            visibilityImageOn.setVisible(passwordVisible);
        }
    }

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
