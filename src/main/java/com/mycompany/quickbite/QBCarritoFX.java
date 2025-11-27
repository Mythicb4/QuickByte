package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class QBCarritoFX {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private StackPane SPProduct4;

    @FXML
    private StackPane SPProduct41;

    @FXML
    private StackPane SPProduct411;

    @FXML
    private StackPane SPProduct42;

    @FXML
    private StackPane SPProduct43;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAdd1;

    @FXML
    private Button btnAdd11;

    @FXML
    private Button btnAdd2;

    @FXML
    private Button btnAdd3;

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnComboE;

    @FXML
    private Button btnConfirmar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnEliminar1;

    @FXML
    private Button btnEliminar11;

    @FXML
    private Button btnEliminar2;

    @FXML
    private Button btnEliminar3;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnMinus;

    @FXML
    private Button btnMinus1;

    @FXML
    private Button btnMinus11;

    @FXML
    private Button btnMinus2;

    @FXML
    private Button btnMinus3;

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
    private ImageView imgProduct11;

    @FXML
    private ImageView imgProduct2;

    @FXML
    private ImageView imgProduct3;

    @FXML
    private Label lblAmount;

    @FXML
    private Label lblAmount1;

    @FXML
    private Label lblAmount11;

    @FXML
    private Label lblAmount2;

    @FXML
    private Label lblAmount3;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private Label lblPName;

    @FXML
    private Label lblPName1;

    @FXML
    private Label lblPName11;

    @FXML
    private Label lblPName2;

    @FXML
    private Label lblPName3;

    @FXML
    private Label lblPPrice;

    @FXML
    private Label lblPPrice1;

    @FXML
    private Label lblPPrice11;

    @FXML
    private Label lblPPrice2;

    @FXML
    private Label lblPPrice3;

    @FXML
    private Text txtTotal;

    @FXML
    void onAdd(ActionEvent event) {

    }

    @FXML
    void onComboE(ActionEvent event) {

    }

    @FXML
    void onConfirmar(ActionEvent event) {

    }

    @FXML
    void onEliminar(ActionEvent event) {

    }

    @FXML
    void onMinus(ActionEvent event) {

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
