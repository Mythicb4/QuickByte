package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.PreferenciasDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.CarritoManager;
import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Optional;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QBPreferenciasFX implements Initializable {

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

    // internal lists
    private List<Product> prefProducts;
    private List<StackPane> productStackPanes;
    private List<Label> productNameLabels;
    private List<Label> productPriceLabels;
    private List<ImageView> productImages;
    private List<Button> addButtons;
    private List<Button> expressButtons;

    @FXML
    void onAñadir(ActionEvent event) {
        Object src = event.getSource();
        if (!(src instanceof Button))
            return;
        int idx = addButtons != null ? addButtons.indexOf((Button) src) : -1;
        if (idx < 0)
            return;
        if (prefProducts == null || idx >= prefProducts.size())
            return;
        Product p = prefProducts.get(idx);
        addProductToCart(p, 1);
    }

    @FXML
    void onAñadirExpress(ActionEvent event) {
        // In preferences view this button removes the product from preferences
        Object src = event.getSource();
        if (!(src instanceof Button))
            return;
        int idx = expressButtons != null ? expressButtons.indexOf((Button) src) : -1;
        if (idx < 0)
            return;
        if (prefProducts == null || idx >= prefProducts.size())
            return;
        Product p = prefProducts.get(idx);

        // Ask for confirmation before removing
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas eliminar '" + p.getName() + "' de tus preferencias?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar preferencia");
        Optional<ButtonType> res = confirm.showAndWait();
        if (!res.isPresent() || res.get() != ButtonType.YES) {
            return; // cancelled
        }

        try {
            String user = AppState.getUserEmail();
            if (user != null && !user.trim().isEmpty()) {
                PreferenciasDao pd = new PreferenciasDao(user);
                pd.removePreferenceByName(p.getName());
                loadPreferencesAndDisplay();
            }
        } catch (Exception e) {
            System.err.println("Error eliminando preferencia: " + e.getMessage());
        }
    }

    private void addProductToCart(Product product, int quantity) {
        if (product == null)
            return;
        String user = AppState.getUserEmail();
        if (user == null)
            user = "anon";

        OrdenarProducto item = new OrdenarProducto(user, product.getName(), product.getPrice(), quantity);
        CarritoManager.getInstancia().addItem(item);
        String mensaje = String.format(
                "Producto añadido\nUsuario: %s\nProducto: %s\nPrecio Unitario: $%.2f\nCantidad: %d\nTotal Actual del Carrito: %d ítems.",
                user, item.getProductName(), item.getPrice(), item.getQuantity(),
                CarritoManager.getInstancia().getItems().size());
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
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
        // Nota: El método handleLogout anterior vaciaba el carrito.
        // Si el botón Salir debe vaciar el carrito, puedes añadir:
        // CarritoManager.getInstancia().vaciarCarrito();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLists();
        // start with nothing visible
        hideAllProductPanes();
        loadPreferencesAndDisplay();
    }

    private void initializeLists() {
        productStackPanes = new ArrayList<>();
        productStackPanes.add(SPProduct4);
        productStackPanes.add(SPProduct41);
        productStackPanes.add(SPProduct42);
        productStackPanes.add(SPProduct1);
        productStackPanes.add(SPProduct43);

        productNameLabels = new ArrayList<>();
        productNameLabels.add(lblPName);
        productNameLabels.add(lblPName1);
        productNameLabels.add(lblPName2);
        productNameLabels.add(lblName);
        productNameLabels.add(lblPName3);

        productPriceLabels = new ArrayList<>();
        productPriceLabels.add(lblPPrice);
        productPriceLabels.add(lblPPrice1);
        productPriceLabels.add(lblPPrice2);
        productPriceLabels.add(null); // SPProduct1 uses lblEstado instead
        productPriceLabels.add(lblPPrice3);

        productImages = new ArrayList<>();
        productImages.add(imgProduct);
        productImages.add(imgProduct1);
        productImages.add(imgProduct2);
        productImages.add(null);
        productImages.add(imgProduct3);

        addButtons = new ArrayList<>();
        addButtons.add(btnAñadir);
        addButtons.add(btnAñadir1);
        addButtons.add(btnAñadir2);
        addButtons.add(btnAñadir3);
        addButtons.add(btnAñadir3); // reuse if fewer

        expressButtons = new ArrayList<>();
        expressButtons.add(btnAñadirExpress);
        expressButtons.add(btnAñadirExpress1);
        expressButtons.add(btnAñadirExpress2);
        expressButtons.add(btnAñadirExpress3);
        expressButtons.add(btnAñadirExpress3);
    }

    private void hideAllProductPanes() {
        if (productStackPanes == null)
            return;
        for (StackPane sp : productStackPanes) {
            if (sp != null)
                sp.setVisible(false);
        }
    }

    private void loadPreferencesAndDisplay() {
        try {
            String user = AppState.getUserEmail();
            if (user == null || user.trim().isEmpty())
                return;
            PreferenciasDao pd = new PreferenciasDao(user);
            prefProducts = pd.loadAll();
            // display up to available panes
            hideAllProductPanes();
            if (prefProducts == null || prefProducts.isEmpty())
                return;
            for (int i = 0; i < prefProducts.size() && i < productStackPanes.size(); i++) {
                Product p = prefProducts.get(i);
                StackPane sp = productStackPanes.get(i);
                Label nameLbl = productNameLabels.size() > i ? productNameLabels.get(i) : null;
                Label priceLbl = productPriceLabels.size() > i ? productPriceLabels.get(i) : null;
                ImageView iv = productImages.size() > i ? productImages.get(i) : null;
                if (sp != null)
                    sp.setVisible(true);
                if (nameLbl != null)
                    nameLbl.setText(p.getName());
                if (priceLbl != null)
                    priceLbl.setText("$ " + p.getPrice());
                if (iv != null && p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                    try {
                        Image img = p.getImagePath().startsWith("http") || p.getImagePath().startsWith("file:")
                                ? new Image(p.getImagePath(), true)
                                : new Image("file:" + p.getImagePath(), true);
                        if (!img.isError())
                            iv.setImage(img);
                    } catch (Exception e) {
                        System.err.println("No se pudo cargar imagen preferencia: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando preferencias: " + e.getMessage());
        }
    }
}
