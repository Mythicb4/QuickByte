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
import javafx.scene.input.MouseEvent;

public class QBFacturacionFX {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnEstablecerFecha;

    @FXML
    private TableColumn<?, ?> colCambio;

    @FXML
    private TableColumn<?, ?> colCliente;

    @FXML
    private TableColumn<?, ?> colFecha;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colMetodo;

    @FXML
    private TableColumn<?, ?> colProductos;

    @FXML
    private TableColumn<?, ?> colRecibido;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private ImageView imgFechaInicio;

    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgSearch;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private TableView<?> tblFacturacion;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TextField txtFechaFin;

    @FXML
    private TextField txtFechaInicio;

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
    void onCalendar(MouseEvent event) {

    }

    @FXML
    void onEstablecerFechaAction(ActionEvent event) {

    }

}
