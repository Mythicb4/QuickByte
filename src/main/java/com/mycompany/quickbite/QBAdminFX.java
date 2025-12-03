package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.BusinessDao;
import com.mycompany.quickbite.dao.StudentDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.model.Student;
import com.mycompany.quickbite.util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QBAdminFX implements Initializable {

    @FXML
    private TableView<Business> tblNegocios;
    @FXML
    private TableColumn<Business, String> colNegocioNombre;
    @FXML
    private TableColumn<Business, String> colNegocioUbicacion;
    @FXML
    private TableColumn<Business, String> colNegocioEmail;
    @FXML
    private TableColumn<Business, String> colNegocioPassword;

    @FXML
    private TableView<Student> tblEstudiantes;
    @FXML
    private TableColumn<Student, String> colEstudianteNombre;
    @FXML
    private TableColumn<Student, String> colEstudianteEmail;
    @FXML
    private TableColumn<Student, String> colEstudiantePassword;

    @FXML
    private Button btnSalir;

    private BusinessDao businessDao;
    private StudentDao studentDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar DAOs
        businessDao = new BusinessDao();
        studentDao = new StudentDao();

        // Configurar columnas de la tabla de Negocios
        colNegocioNombre.setCellValueFactory(new PropertyValueFactory<>("businessName"));
        colNegocioUbicacion.setCellValueFactory(new PropertyValueFactory<>("location"));
        colNegocioEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNegocioPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Configurar columnas de la tabla de Estudiantes
        colEstudianteNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEstudianteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEstudiantePassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Cargar datos
        loadBusinesses();
        loadStudents();
    }

    private void loadBusinesses() {
        try {
            List<Business> businesses = businessDao.getAllBusinesses();
            ObservableList<Business> businessList = FXCollections.observableArrayList(businesses);
            tblNegocios.setItems(businessList);
        } catch (Exception e) {
            showError("Error al cargar negocios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDao.getAllStudents();
            ObservableList<Student> studentList = FXCollections.observableArrayList(students);
            tblEstudiantes.setItems(studentList);
        } catch (Exception e) {
            showError("Error al cargar estudiantes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onBtnSalir(ActionEvent event) {
        Navigator.navigateTo("/views/login.fxml", "QuickBite - Login", true, event);
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadBusinesses();
        loadStudents();
        showInfo("Datos actualizados correctamente");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
