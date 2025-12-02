package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.BusinessDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.CarritoManager;
import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QBTiendaFX implements Initializable {

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

    private BusinessDao businessDao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        businessDao = new BusinessDao();
        cargarCafeterias();
    }

    /**
     * Carga cafeterías desde la BD y las muestra en la vista.
     * Oculta las tarjetas que no tengan datos.
     */
    private void cargarCafeterias() {
        List<Business> businesses = businessDao.getAllBusinesses();

        // Ocultar todos los StackPane inicialmente
        ocultarTodasLasTarjetas();

        // Mapear cafeterías a tarjetas
        if (businesses.size() > 0) {
            llenarTarjeta(0, businesses.get(0), SPProduct, lblName, lblUbicacion, lblEstado, imgCafeteria, btnEntrar);
        }
        if (businesses.size() > 1) {
            llenarTarjeta(1, businesses.get(1), SPProduct2, lblName1, lblUbicacion1, lblEstado1, imgCafeteria1,
                    btnEntrar1);
        }
        if (businesses.size() > 2) {
            llenarTarjeta(2, businesses.get(2), SPProduct3, lblName2, lblUbicacion2, lblEstado2, imgCafeteria2,
                    btnEntrar2);
        }
    }

    /**
     * Llena una tarjeta con los datos de una cafetería.
     */
    private void llenarTarjeta(int index, Business business, StackPane stackPane, Label lblName,
            Label lblUbicacion, Label lblEstado, ImageView imgCafeteria, Button btnEntrar) {
        // Mostrar la tarjeta
        stackPane.setVisible(true);

        // Llenar datos
        lblName.setText(business.getBusinessName());
        lblUbicacion.setText(business.getLocation());

        // Por ahora, estado simulado (puedes cambiar a true/false de la BD si la
        // tienes)
        String estado = "Abierto";
        lblEstado.setText(estado);

        // Guardar la referencia del negocio cuando se presione "Entrar"
        btnEntrar.setOnAction(event -> {
            AppState.setSelectedBusiness(business);
            onEntrarAction(event);
        });

        // Cargar imagen del negocio si existe
        try {
            if (business.getImagePath() != null && !business.getImagePath().isBlank()) {
                // Si la ruta es absoluta en Windows, usar prefijo file:
                String path = business.getImagePath();
                if (!path.startsWith("file:")) {
                    path = "file:" + path;
                }
                imgCafeteria.setImage(new javafx.scene.image.Image(path));
            }
        } catch (Exception e) {
            // ignore - keep default image
        }
    }

    /**
     * Oculta todas las tarjetas de cafeterías.
     */
    private void ocultarTodasLasTarjetas() {
        SPProduct.setVisible(false);
        SPProduct1.setVisible(false);
        SPProduct2.setVisible(false);
        SPProduct3.setVisible(false);
    }

    @FXML
    void onBuscarAction(ActionEvent event) {
        // Implementar búsqueda de cafeterías
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
    private void handleViewCartAndGenerateQR(ActionEvent event) {

        // Verificar si hay productos en el carrito antes de navegar.
        if (CarritoManager.getInstancia().getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El carrito está vacío. Agregue productos antes de generar el QR.")
                    .showAndWait();
            return;
        }

        Navigator.navigateTo("/views/carrito.fxml", "QuickBite - Mi Carrito", false, event);
    }

    @FXML
    void onBtnSalir(ActionEvent event) {
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
    }

    @FXML
    void onHistorial(ActionEvent event) {
        Navigator.navigateTo("/views/historial_estudiante.fxml", "historial", true, event);
    }

    @FXML
    void onPerfil(ActionEvent event) {
        Navigator.navigateTo("/views/perfil_estudiante.fxml", "perfil", false, event);
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