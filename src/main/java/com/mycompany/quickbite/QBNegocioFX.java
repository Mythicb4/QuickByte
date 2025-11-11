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
    void onBtnProduct(ActionEvent event) {
        Navigator.navigateTo("/views/producto_negocio.fxml", "products", event);
    }

    @FXML
    void onBtnSalir(ActionEvent event) {

    }

}
