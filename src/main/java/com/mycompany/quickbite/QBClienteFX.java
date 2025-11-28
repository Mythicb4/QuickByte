package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ClientDao;
import com.mycompany.quickbite.model.Client;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext; // Importamos tu clase

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class QBClienteFX {

    @FXML private Button btnAdd;
    @FXML private Button btnAtras;
    @FXML private Button btnEdit;
    @FXML private Button btnRemove;
    @FXML private ImageView imgLogo;
    @FXML private ImageView imgSearch;
    @FXML private Label lblLogo;
    @FXML private Label lblLogo1;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Client> tblClientes;
    @FXML private TableColumn<Client, String> colCedula;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colTelefono;

    private ClientDao clientDao;
    private ObservableList<Client> clientList;

    @FXML
    public void initialize() {
        try {
            // 1. USAMOS SESSIONCONTEXT PARA OBTENER EL EMAIL
            String businessEmail = SessionContext.getLoggedInBusinessEmail();
            
            // 2. Inicializamos el DAO con ese email
            clientDao = new ClientDao(businessEmail);

            // 3. Configurar columnas
            colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

            // 4. Cargar datos
            loadData();
            
        } catch (IllegalStateException e) {
            // Si no hay sesión iniciada (al probar directamente la vista sin login)
            showAlert(Alert.AlertType.ERROR, "Error de Sesión", "No se detectó un usuario logueado. " + e.getMessage());
            btnAdd.setDisable(true);
            btnEdit.setDisable(true);
            btnRemove.setDisable(true);
        } catch (Exception e) {
             showAlert(Alert.AlertType.ERROR, "Error Crítico", "Error al inicializar módulo de clientes: " + e.getMessage());
        }
    }

    private void loadData() {
        if (clientDao != null) {
            clientList = FXCollections.observableArrayList(clientDao.loadAll());
            tblClientes.setItems(clientList);
        }
    }

    @FXML
    void onBuscarAction(ActionEvent event) {
        String query = txtBuscar.getText();
        if (clientDao != null) {
            clientList = FXCollections.observableArrayList(clientDao.search(query));
            tblClientes.setItems(clientList);
        }
    }

    @FXML
    void onAdd(ActionEvent event) {
        showClientDialog(null);
    }

    @FXML
    void onEdit(ActionEvent event) {
        Client selected = tblClientes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un cliente para editar.");
            return;
        }
        showClientDialog(selected);
    }

    @FXML
    void onRemove(ActionEvent event) {
        Client selected = tblClientes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un cliente para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar Cliente");
        confirm.setHeaderText("¿Eliminar a " + selected.getNombre() + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                clientDao.removeClient(selected.getId());
                loadData();
                showAlert(Alert.AlertType.INFORMATION, "Eliminado", "Cliente eliminado correctamente.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    @FXML
    void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }

    // --- Métodos de Ayuda ---

    private void showClientDialog(Client clientToEdit) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(clientToEdit == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.setHeaderText("Ingrese los datos del cliente");

        ButtonType saveBtn = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        TextField txtNombre = new TextField(); txtNombre.setPromptText("Nombre");
        TextField txtCedula = new TextField(); txtCedula.setPromptText("Cédula");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email");
        TextField txtTelefono = new TextField(); txtTelefono.setPromptText("Teléfono");

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Cédula:"), txtCedula);
        grid.addRow(2, new Label("Email:"), txtEmail);
        grid.addRow(3, new Label("Teléfono:"), txtTelefono);

        if (clientToEdit != null) {
            txtNombre.setText(clientToEdit.getNombre());
            txtCedula.setText(clientToEdit.getCedula());
            txtEmail.setText(clientToEdit.getEmail());
            txtTelefono.setText(clientToEdit.getTelefono());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if(txtNombre.getText().isEmpty() || txtCedula.getText().isEmpty()) return null;

                Client c = (clientToEdit != null) ? clientToEdit : new Client();
                c.setNombre(txtNombre.getText());
                c.setCedula(txtCedula.getText());
                c.setEmail(txtEmail.getText());
                c.setTelefono(txtTelefono.getText());
                return c;
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                if (c.getNombre().isEmpty()) throw new Exception("El nombre es obligatorio");
                
                if (clientToEdit == null) {
                    clientDao.addClient(c);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Cliente creado.");
                } else {
                    clientDao.updateClient(c);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Cliente actualizado.");
                }
                loadData();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}