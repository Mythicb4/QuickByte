package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.BusinessDao;
import com.mycompany.quickbite.dao.StudentDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.model.Student;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class QBsingupFX {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnCrear;

    @FXML
    private Label lblLogin;

    @FXML
    private ImageView logoImage;
    
    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfPasswordVisible;

    @FXML
    private TextField txtBussines;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtLocation;

    @FXML
    private ImageView userImage;

    @FXML
    private ImageView userImage1;

    @FXML
    private ImageView userImage2;

    @FXML
    private VBox vbLogin;

    @FXML
    private ImageView visibilityImage;
    
    @FXML
    private ImageView visibilityImageOff;

    @FXML
    private ImageView visibilityImageOn;

    @FXML
    private void onBackClick(ActionEvent event) {
        AppState.setUserType(null);
        Navigator.navigateTo("/views/type.fxml", "type", true, event);
    }
    
    // indica si la contraseña está visible (texto plano)
    private boolean passwordVisible = false;
    private boolean isBusiness = false;

    @FXML
    private void initialize() {
         String type = AppState.getUserType();

        // Si el AppState no se setea (por usar fxmls distintos), lo detectamos por nombre del FXML cargado
        if ("negocio".equalsIgnoreCase(type)) {
            isBusiness = true;
        } else {
            isBusiness = false;
        }

        System.out.println("Modo signup: " + (isBusiness ? "Negocio" : "Estudiante"));

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
    
    @FXML
    private void onCrearClick(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = tfPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Por favor completa todos los campos obligatorios.");
            return;
        }

        if (isBusiness) {
            String name = txtBussines.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                System.out.println("Completa nombre y ubicación del negocio.");
                return;
            }

            BusinessDao bDao = new BusinessDao();
            if (bDao.emailExists(email)) {
                System.out.println("Email ya registrado.");
                return;
            }

            bDao.addBusiness(new Business(name, location, email, password));
            System.out.println("Negocio registrado correctamente.");
            Navigator.navigateTo("/views/login.fxml", "login", true, event);
        } else {
            StudentDao sDao = new StudentDao();
            if (sDao.emailExists(email)) {
                System.out.println("Email ya registrado.");
                return;
            }

            sDao.addStudent(new Student(email, password));
            System.out.println("Estudiante registrado correctamente.");
            Navigator.navigateTo("/views/login.fxml", "login", true, event);
        }
    }
}
