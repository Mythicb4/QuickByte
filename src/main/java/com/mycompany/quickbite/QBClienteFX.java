package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class QBClienteFX {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnRemove;

    @FXML
    private TableColumn<?, ?> colCedula;

    @FXML
    private TableColumn<?, ?> colEmail;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colTelefono;

    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgSearch;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private TableView<?> tblClientes;

    @FXML
    private TextField txtBuscar;

    @FXML
    void onAdd(ActionEvent event) {

    }

    @FXML
    void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }

    @FXML
    void onBuscarAction(ActionEvent event) {

    }

    @FXML
    void onEdit(ActionEvent event) {

    }

    @FXML
    void onRemove(ActionEvent event) {

    }

}
