package com.mycompany.quickbite;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import com.mycompany.quickbite.dao.BusinessDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.Navigator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.io.File;

public class QBConfiguracionFX implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblEmail;

    @FXML
    private TextField txtContraseña;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtUbicacion;

    @FXML
    private TextField txtImagePath;

    @FXML
    private Button btnBrowse;

    private BusinessDao businessDao = new BusinessDao();

    @FXML
    void onCancel(ActionEvent event) {
        // Close the configuration window. It's typically opened as a separate stage.
        if (btnCancel != null && btnCancel.getScene() != null) {
            btnCancel.getScene().getWindow().hide();
        }
    }

    @FXML
    void onDeleteAction(ActionEvent event) {
        String email = AppState.getUserEmail();
        if (email == null)
            return;

        // Confirm deletion with the user
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminarán todos los datos del negocio (productos, ventas y clientes). ¿Desea continuar?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar cuenta");
        Optional<ButtonType> result = confirm.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.YES) {
            // user cancelled
            return;
        }

        // 1) Remove business entry
        businessDao.deleteBusinessByEmail(email);

        // 2) Remove product file, ventas file and clientes file for this business
        try {
            String safe = email.toLowerCase().replace("@", "_at_").replace(".", "_dot_");
            java.nio.file.Path products = java.nio.file.Paths.get("data", "productos_negocio", safe + ".json");
            java.nio.file.Path ventas = java.nio.file.Paths.get("data", "ventas_negocio", "ventas_" + safe + ".json");
            java.nio.file.Path clientes = java.nio.file.Paths.get("data", "clientes_negocio",
                    "clients_" + safe + ".json");

            java.nio.file.Files.deleteIfExists(products);
            java.nio.file.Files.deleteIfExists(ventas);
            java.nio.file.Files.deleteIfExists(clientes);
        } catch (Exception e) {
            // log and continue
            System.err.println("Error borrando archivos del negocio: " + e.getMessage());
        }

        // 3) Clear app state and close the config window
        AppState.setSelectedBusiness(null);
        AppState.setUserEmail(null);

        // Close window
        if (btnCancel != null && btnCancel.getScene() != null) {
            btnCancel.getScene().getWindow().hide();
        }

        // Navigate user to login screen
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
    }

    @FXML
    void onSave(ActionEvent event) {
        String email = AppState.getUserEmail();
        if (email == null)
            return;
        Business current = null;
        for (Business b : businessDao.getAllBusinesses()) {
            if (b.getEmail() != null && b.getEmail().equalsIgnoreCase(email)) {
                current = b;
                break;
            }
        }
        if (current == null) {
            current = new Business();
            current.setEmail(email);
        }

        current.setBusinessName(txtName.getText());
        current.setPassword(txtContraseña.getText());
        current.setLocation(txtUbicacion.getText());
        current.setImagePath(txtImagePath.getText());

        businessDao.updateBusiness(current);
        AppState.setSelectedBusiness(current);
        // Show success alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración guardada");
        alert.setHeaderText(null);
        alert.setContentText("Los datos del negocio se han guardado correctamente.");
        alert.showAndWait();

        // Close the configuration window
        if (btnSave != null && btnSave.getScene() != null) {
            btnSave.getScene().getWindow().hide();
        }
    }

    @FXML
    void onBrowseImage(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen del negocio");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            txtImagePath.setText(file.getAbsolutePath());
        }
    }

    // Optional: call this from where config view is opened to preload current
    // business data
    public void loadCurrentBusiness() {
        String email = AppState.getUserEmail();
        if (email == null)
            return;
        for (Business b : businessDao.getAllBusinesses()) {
            if (b.getEmail() != null && b.getEmail().equalsIgnoreCase(email)) {
                txtName.setText(b.getBusinessName());
                txtContraseña.setText(b.getPassword());
                txtUbicacion.setText(b.getLocation());
                txtImagePath.setText(b.getImagePath());
                lblEmail.setText(b.getEmail());
                AppState.setSelectedBusiness(b);
                break;
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCurrentBusiness();
    }

}
