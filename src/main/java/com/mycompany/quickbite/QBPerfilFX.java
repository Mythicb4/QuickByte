package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.PreferenciasDao;
import com.mycompany.quickbite.dao.StudentDao;
import com.mycompany.quickbite.dao.StudentPurchaseDao;
import com.mycompany.quickbite.model.Student;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.CarritoManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class QBPerfilFX implements Initializable {

    private StudentDao studentDao;
    private Student currentStudent;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnTienda;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    @FXML
    private TextField txtPassword;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        studentDao = new StudentDao();
        loadStudentData();
    }

    private void loadStudentData() {
        try {
            String email = AppState.getUserEmail();
            if (email == null || email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "No se pudo cargar los datos: usuario no identificado.").showAndWait();
                return;
            }

            // Buscar estudiante por email
            List<Student> students = studentDao.getAllStudents();
            currentStudent = students.stream()
                    .filter(s -> s.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);

            if (currentStudent != null) {
                txtPassword.setText(currentStudent.getPassword());
                lblEmail.setText(currentStudent.getEmail());
            } else {
                new Alert(Alert.AlertType.ERROR, "No se encontró el estudiante.").showAndWait();
            }

        } catch (Exception e) {
            System.err.println("Error cargando datos del estudiante: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar los datos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            String newPassword = txtPassword.getText().trim();

            // Validaciones
            if (newPassword.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "La contraseña no puede estar vacía.").showAndWait();
                return;
            }

            // Actualizar solo la contraseña (el email no se puede modificar)
            currentStudent.setPassword(newPassword);

            // Guardar en el archivo JSON
            List<Student> allStudents = studentDao.getAllStudents();
            for (int i = 0; i < allStudents.size(); i++) {
                if (allStudents.get(i).getEmail().equalsIgnoreCase(currentStudent.getEmail())) {
                    allStudents.set(i, currentStudent);
                    break;
                }
            }

            // Usar reflexión para acceder al método privado saveAll
            java.lang.reflect.Method saveAllMethod = StudentDao.class.getDeclaredMethod("saveAll", List.class);
            saveAllMethod.setAccessible(true);
            saveAllMethod.invoke(studentDao, allStudents);

            new Alert(Alert.AlertType.INFORMATION, "Perfil actualizado exitosamente.").showAndWait();

        } catch (Exception e) {
            System.err.println("Error guardando perfil: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        // Recargar datos originales
        loadStudentData();
        new Alert(Alert.AlertType.INFORMATION, "Cambios cancelados.").showAndWait();
    }

    @FXML
    void onDeleteAccount(ActionEvent event) {
        try {
            // Confirmación
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmar Eliminación");
            confirmAlert.setHeaderText("¿Está seguro de eliminar su cuenta?");
            confirmAlert.setContentText(
                    "Esta acción eliminará:\n- Su información de estudiante\n- Sus preferencias\n- Su historial de compras\n\nEsta acción NO se puede deshacer.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                String email = currentStudent.getEmail();

                // 1. Eliminar del students.json
                List<Student> allStudents = studentDao.getAllStudents();
                allStudents.removeIf(s -> s.getEmail().equalsIgnoreCase(email));

                java.lang.reflect.Method saveAllMethod = StudentDao.class.getDeclaredMethod("saveAll", List.class);
                saveAllMethod.setAccessible(true);
                saveAllMethod.invoke(studentDao, allStudents);

                // 2. Eliminar archivo de preferencias
                String safeEmail = email.toLowerCase()
                        .replace("@", "_at_")
                        .replace(".", "_dot_");
                Path preferencesPath = Paths.get("data", "preferencias_estudiante",
                        "preferencias_" + safeEmail + ".json");
                try {
                    Files.deleteIfExists(preferencesPath);
                    System.out.println("Archivo de preferencias eliminado: " + preferencesPath);
                } catch (IOException e) {
                    System.err.println("Error eliminando preferencias: " + e.getMessage());
                }

                // 3. Eliminar archivo de historial
                Path historyPath = Paths.get("data", "historial_estudiante", "compras_" + safeEmail + ".json");
                try {
                    Files.deleteIfExists(historyPath);
                    System.out.println("Archivo de historial eliminado: " + historyPath);
                } catch (IOException e) {
                    System.err.println("Error eliminando historial: " + e.getMessage());
                }

                // 4. Limpiar sesión y carrito
                CarritoManager.getInstancia().vaciarCarrito();

                // 5. Mostrar mensaje y redirigir a login
                new Alert(Alert.AlertType.INFORMATION,
                        "Su cuenta ha sido eliminada exitosamente.").showAndWait();

                Navigator.navigateTo("/views/login.fxml", "login", true, event);
            }

        } catch (Exception e) {
            System.err.println("Error eliminando cuenta: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al eliminar la cuenta: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onAtras(ActionEvent event) {
        Navigator.navigateTo("/views/login_estudiante.fxml", "estudiante", true, event);
    }

    @FXML
    private void handleViewCartAndGenerateQR(ActionEvent event) {

        // Verificar si hay productos en el carrito antes de navegar.
        if (CarritoManager.getInstancia().getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El carrito está vacío. Agregue productos antes de generar el QR.")
                    .showAndWait();
            return;
        }

        Navigator.navigateTo("/views/carrito.fxml", "QuickBite - Mi Carrito", false, event);
    }

    @FXML
    void onBtnSalir(ActionEvent event) {
        // Nota: El método handleLogout anterior vaciaba el carrito.
        // Si el botón Salir debe vaciar el carrito, puedes añadir:
        // CarritoManager.getInstancia().vaciarCarrito();
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
    }

    @FXML
    void onHistorial(ActionEvent event) {
        Navigator.navigateTo("/views/historial_estudiante.fxml", "historial", true, event);
    }

    @FXML
    void onPerfil(ActionEvent event) {
        Navigator.navigateTo("/views/perfil_estudiante.fxml", "perfil", true, event);
    }

    @FXML
    void onPreferencias(ActionEvent event) {
        Navigator.navigateTo("/views/preferencia_estudiante.fxml", "preferencia", true, event);
    }

    @FXML
    void onTienda(ActionEvent event) {
        Navigator.navigateTo("/views/tiendas_estudiante.fxml", "tiendas", true, event);
    }
}
