package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.BusinessDao;
import com.mycompany.quickbite.dao.StudentDao;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
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

    @FXML
    private RadioButton rbEstudiante;

    @FXML
    private RadioButton rbNegocio;

    // indica si la contraseña está visible (texto plano)
    private boolean passwordVisible = false;

    // guarda si eligió estudiante o negocio
    private String selectedType = null;

    @FXML
    private void onEstudianteSelected(ActionEvent event) {
        selectedType = "estudiante";
    }

    @FXML
    private void onNegocioSelected(ActionEvent event) {
        selectedType = "negocio";
    }

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

        // 2) asegurar estado inicial de iconos (off = ojo cerrado visible; on = ojo
        // abierto oculto)
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
                tfPasswordVisible
                        .positionCaret(tfPasswordVisible.getText() != null ? tfPasswordVisible.getText().length() : 0);
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
    private void onLoginClick(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = tfPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Por favor ingresa email y contraseña.");
            return;
        }

        if (selectedType == null) {
            System.out.println("⚠️ Selecciona el tipo de usuario primero.");
            return;
        }

        boolean valid = false;

        if (selectedType.equals("negocio")) {
            // Guardar el email en la sesión
            SessionContext.setLoggedInBusinessEmail(email);
            BusinessDao dao = new BusinessDao();
            valid = dao.validateCredentials(email, password);
        } else if (selectedType.equals("estudiante")) {
            StudentDao dao = new StudentDao();
            valid = dao.validateCredentials(email, password);
        }

        if (valid) {
            System.out.println("✅ Bienvenido, " + selectedType + "!");
            // Guardar información mínima de sesión/global para otras vistas
            AppState.setUserType(selectedType);
            AppState.setUserEmail(email);

            if (selectedType.equals("negocio")) {
                Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
            } else {
                Navigator.navigateTo("/views/login_estudiante.fxml", "dashboard_estudiante", true, event);
            }
        } else {
            System.out.println("❌ Email o contraseña incorrectos.");
        }
    }

    // Evento del botón "Singup"
    @FXML
    private void onBtnSingup(ActionEvent event) {
        Navigator.navigateTo("/views/type.fxml", "type", true, event);
    }

    // Evento del Hyperlink "Recuperar contraseña"
    @FXML
    private void onLinkForgot(ActionEvent event) {
        System.out.println("Recuperar contraseña");
    }
}
