package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class QBPreferenciasFX {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private StackPane SPProduct1;

    @FXML
    private StackPane SPProduct4;

    @FXML
    private StackPane SPProduct41;

    @FXML
    private StackPane SPProduct42;

    @FXML
    private StackPane SPProduct43;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnAñadir;

    @FXML
    private Button btnAñadir1;

    @FXML
    private Button btnAñadir2;

    @FXML
    private Button btnAñadir3;

    @FXML
    private Button btnAñadirExpress;

    @FXML
    private Button btnAñadirExpress1;

    @FXML
    private Button btnAñadirExpress2;

    @FXML
    private Button btnAñadirExpress3;

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
    private ImageView imgLogo;

    @FXML
    private ImageView imgProduct;

    @FXML
    private ImageView imgProduct1;

    @FXML
    private ImageView imgProduct2;

    @FXML
    private ImageView imgProduct3;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPName;

    @FXML
    private Label lblPName1;

    @FXML
    private Label lblPName2;

    @FXML
    private Label lblPName3;

    @FXML
    private Label lblPPrice;

    @FXML
    private Label lblPPrice1;

    @FXML
    private Label lblPPrice2;

    @FXML
    private Label lblPPrice3;

    @FXML
    void onAñadir(ActionEvent event) {

    }

    @FXML
    void onAñadirExpress(ActionEvent event) {

    }

    @FXML
    void onAtras(ActionEvent event) {
        Navigator.navigateTo("/views/login_estudiante.fxml", "estudiante", true, event);
    }

    @FXML
    void onCarrito(ActionEvent event) {
        Navigator.navigateTo("/views/carrito_estudiante.fxml", "carrito", true, event);
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
