package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class QBInventarioFX {

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnFacturacion;

    @FXML
    private Button btnInventario;

    @FXML
    private Button btnProduct;

    @FXML
    private Button btnQr;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnVentas;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colMinStock;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colProvider;

    @FXML
    private TableColumn<?, ?> colStado;

    @FXML
    private TableColumn<?, ?> colStock;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private TableView<?> tblProductos;

    @FXML
    void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }

    @FXML
    void onClientes(ActionEvent event) {
        Navigator.navigateTo("/views/cliente_negocio.fxml", "cliente", true, event);
    }

    @FXML
    void onConfiguracion(ActionEvent event) {
        Navigator.navigateTo("/views/configuracion_negocio.fxml", "configuracion", false, event);
    }

    @FXML
    void onFacturacion(ActionEvent event) {
        Navigator.navigateTo("/views/facturacion_negocio.fxml", "facturacion", true, event);
    }

    @FXML
    void onInventario(ActionEvent event) {
        Navigator.navigateTo("/views/inventario_negocio.fxml", "inventario", true, event);
    }

    @FXML
    void onProduct(ActionEvent event) {
        Navigator.navigateTo("/views/producto_negocio.fxml", "producto", true, event);
    }

    @FXML
    void onQr(ActionEvent event) {

    }

    @FXML
    void onReportes(ActionEvent event) {
        Navigator.navigateTo("/views/reportes_negocio.fxml", "reportes", true, event);
    }

    @FXML
    void onVentas(ActionEvent event) {
        Navigator.navigateTo("/views/ventas_negocio.fxml", "ventas", false, event);
    }
}
