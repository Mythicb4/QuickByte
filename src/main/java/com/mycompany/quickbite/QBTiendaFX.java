package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class QBTiendaFX {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private StackPane SPProduct;

    @FXML
    private StackPane SPProduct1;

    @FXML
    private StackPane SPProduct2;

    @FXML
    private StackPane SPProduct3;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnEntrar1;

    @FXML
    private Button btnEntrar2;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnTienda;

    @FXML
    private ImageView imgCafeteria;

    @FXML
    private ImageView imgCafeteria1;

    @FXML
    private ImageView imgCafeteria2;

    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgSearch;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblEstado1;

    @FXML
    private Label lblEstado2;

    @FXML
    private Label lblEstado3;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private Label lblName;

    @FXML
    private Label lblName1;

    @FXML
    private Label lblName2;

    @FXML
    private Label lblUbicacion;

    @FXML
    private Label lblUbicacion1;

    @FXML
    private Label lblUbicacion2;

    @FXML
    private TextField txtBuscar;

    @FXML
    void onBuscarAction(ActionEvent event) {

    }

    @FXML
    void onEntrarAction(ActionEvent event) {
        Navigator.navigateTo("/views/cafeteria_estudiante.fxml", "cafeteria", true, event);
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