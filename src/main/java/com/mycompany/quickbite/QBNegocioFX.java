package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class QBNegocioFX {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnHistorial1;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPerfil1;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnSalir;

    @FXML
    private Button btnTienda;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    void onBtnSalir(ActionEvent event) {
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
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
            //Agregar el link a qr para leer
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
