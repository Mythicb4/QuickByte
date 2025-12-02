package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.dao.PreferenciasDao;
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
import javafx.scene.image.Image;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ResourceBundle;

public class QBCafeteriaFX implements Initializable {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private StackPane SPProduct4;

    @FXML
    private StackPane SPProduct41;

    @FXML
    private StackPane SPProduct42;

    @FXML
    private StackPane SPProduct43;

    @FXML
    private StackPane SPProduct44;

    @FXML
    private StackPane SPProduct45;

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
    private Button btnAñadir4;

    @FXML
    private Button btnAñadir5;

    @FXML
    private Button btnAñadirExpress;

    @FXML
    private Button btnAñadirExpress1;

    @FXML
    private Button btnAñadirExpress2;

    @FXML
    private Button btnAñadirExpress3;

    @FXML
    private Button btnAñadirExpress4;

    @FXML
    private Button btnAñadirExpress5;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnCategoria;

    @FXML
    private Button btnCategoria1;

    @FXML
    private Button btnCategoria2;

    @FXML
    private Button btnCategoria3;

    @FXML
    private Button btnCategoria4;

    @FXML
    private Button btnCategoria5;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnTienda;

    @FXML
    private Button btnMostrarTodos;

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
    private ImageView imgProduct4;

    @FXML
    private ImageView imgProduct5;

    @FXML
    private ImageView imgSearch;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private Label lblName;

    @FXML
    private Label lblUbicacion;

    @FXML
    private Label lblPName;

    @FXML
    private Label lblPName1;

    @FXML
    private Label lblPName2;

    @FXML
    private Label lblPName3;

    @FXML
    private Label lblPName4;

    @FXML
    private Label lblPName5;

    @FXML
    private Label lblPPrice;

    @FXML
    private Label lblPPrice1;

    @FXML
    private Label lblPPrice2;

    @FXML
    private Label lblPPrice3;

    @FXML
    private Label lblPPrice4;

    @FXML
    private Label lblPPrice5;

    @FXML
    private Label lblSearch;

    @FXML
    private TextField txtBuscar;

    private ProductDao productDao;
    private List<Product> allProducts;
    private List<Product> currentDisplayedProducts;
    private List<StackPane> productStackPanes;
    private List<Label> productNameLabels;
    private List<Label> productPriceLabels;
    private List<ImageView> productImages;
    private List<Button> addButtons;
    private List<Button> expressButtons;
    private List<Button> categoryButtons;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Business selectedBusiness = AppState.getSelectedBusiness();

        if (selectedBusiness != null) {
            productDao = new ProductDao(selectedBusiness.getEmail());
            // Mostrar información del negocio en la cabecera
            lblName.setText(selectedBusiness.getBusinessName());
            if (lblUbicacion != null) {
                lblUbicacion.setText(selectedBusiness.getLocation());
            }
            initializeProductLists();
            initializeButtonLists();
            // Load all products once and display
            allProducts = productDao.loadAll();
            displayProducts(allProducts);

            // build category buttons from products
            populateCategoryButtons();

            // Add listener to search field for live filtering
            if (txtBuscar != null) {
                txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
                    performSearch(newVal);
                });
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No se seleccionó una cafetería").showAndWait();
        }
    }

    /**
     * Inicializa las listas de componentes de productos para acceso indexado.
     */
    private void initializeProductLists() {
        // StackPanes con 6 tarjetas
        productStackPanes = new ArrayList<>();
        productStackPanes.add(SPProduct4); // 0
        productStackPanes.add(SPProduct41); // 1
        productStackPanes.add(SPProduct42); // 2
        productStackPanes.add(SPProduct43); // 3
        productStackPanes.add(SPProduct44); // 4
        productStackPanes.add(SPProduct45); // 5

        // Labels de nombre
        productNameLabels = new ArrayList<>();
        productNameLabels.add(lblPName); // 0
        productNameLabels.add(lblPName1); // 1
        productNameLabels.add(lblPName2); // 2
        productNameLabels.add(lblPName3); // 3
        productNameLabels.add(lblPName4); // 4
        productNameLabels.add(lblPName5); // 5

        // Labels de precio
        productPriceLabels = new ArrayList<>();
        productPriceLabels.add(lblPPrice); // 0
        productPriceLabels.add(lblPPrice1); // 1
        productPriceLabels.add(lblPPrice2); // 2
        productPriceLabels.add(lblPPrice3); // 3
        productPriceLabels.add(lblPPrice4); // 4
        productPriceLabels.add(lblPPrice5); // 5

        // ImageViews de producto
        productImages = new ArrayList<>();
        productImages.add(imgProduct); // 0
        productImages.add(imgProduct1); // 1
        productImages.add(imgProduct2); // 2
        productImages.add(imgProduct3); // 3
        productImages.add(imgProduct4); // 4
        productImages.add(imgProduct5); // 5
    }

    /**
     * Inicializa listas de botones para manejar indexación de eventos.
     */
    private void initializeButtonLists() {
        addButtons = Arrays.asList(btnAñadir, btnAñadir1, btnAñadir2, btnAñadir3, btnAñadir4, btnAñadir5);
        expressButtons = Arrays.asList(btnAñadirExpress, btnAñadirExpress1, btnAñadirExpress2, btnAñadirExpress3,
                btnAñadirExpress4, btnAñadirExpress5);
        categoryButtons = Arrays.asList(btnCategoria, btnCategoria1, btnCategoria2, btnCategoria3, btnCategoria4,
                btnCategoria5);
    }

    /**
     * Construye dinamicamente los botones de categoria según el orden de aparición
     * en el JSON.
     */
    private void populateCategoryButtons() {
        if (allProducts == null || categoryButtons == null)
            return;

        // Preserve order and uniqueness
        Set<String> categories = new LinkedHashSet<>();
        for (Product p : allProducts) {
            if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                categories.add(p.getCategory().trim());
            }
        }

        List<String> cats = categories.stream().collect(Collectors.toList());

        for (int i = 0; i < categoryButtons.size(); i++) {
            Button b = categoryButtons.get(i);
            if (i < cats.size()) {
                String text = cats.get(i);
                b.setText(text);
                b.setVisible(true);
                // set width proportional to text length (simple heuristic)
                double width = Math.max(80, text.length() * 12);
                b.setPrefWidth(width);
            } else {
                b.setVisible(false);
            }
        }
    }

    /**
     * Carga los productos de la cafetería seleccionada.
     */
    private void cargarProductos() {
        // Backwards-compatible: load from allProducts if available
        if (allProducts == null) {
            allProducts = productDao.loadAll();
        }
        displayProducts(allProducts);
    }

    /**
     * Muestra una lista de productos (hasta la capacidad de tarjetas disponibles).
     */
    private void displayProducts(List<Product> products) {
        ocultarTodasLasTarjetas();
        if (products == null)
            return;
        // remember current displayed list for button handlers
        currentDisplayedProducts = products;
        for (int i = 0; i < products.size() && i < productStackPanes.size(); i++) {
            llenarTarjeta(i, products.get(i));
        }
    }

    /**
     * Filtra la lista completa de productos por nombre (contiene, case-insensitive)
     * y muestra los resultados.
     */
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayProducts(allProducts);
            return;
        }
        String q = query.trim().toLowerCase();
        List<Product> filtered = new ArrayList<>();
        if (allProducts != null) {
            for (Product p : allProducts) {
                if (p.getName() != null && p.getName().toLowerCase().contains(q)) {
                    filtered.add(p);
                }
            }
        }
        displayProducts(filtered);
    }

    /**
     * Llena una tarjeta con los datos de un producto.
     */
    private void llenarTarjeta(int index, Product product) {
        if (index >= productStackPanes.size()) {
            return;
        }

        StackPane stackPane = productStackPanes.get(index);
        Label lblName = productNameLabels.get(index);
        Label lblPrice = productPriceLabels.get(index);
        ImageView img = productImages.get(index);

        // Mostrar la tarjeta
        stackPane.setVisible(true);

        // Llenar datos
        lblName.setText(product.getName());
        lblPrice.setText("$ " + product.getPrice());

        // Cargar la imagen si existe una ruta en el JSON
        String imgPath = product.getImagePath();
        if (imgPath != null && !imgPath.trim().isEmpty()) {
            try {
                Image image;
                if (imgPath.startsWith("http://") || imgPath.startsWith("https://") || imgPath.startsWith("file:")) {
                    image = new Image(imgPath, true);
                } else {
                    // intentamos cargar como archivo local
                    image = new Image("file:" + imgPath, true);
                }
                if (!image.isError()) {
                    img.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen del producto: " + imgPath + " -> " + e.getMessage());
            }
        }

        // Mostrar la descripción como tooltip si existe
        String desc = product.getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            Tooltip tip = new Tooltip(desc);
            Tooltip.install(stackPane, tip);
        }
    }

    /**
     * Oculta todas las tarjetas de productos.
     */
    private void ocultarTodasLasTarjetas() {
        for (StackPane sp : productStackPanes) {
            sp.setVisible(false);
        }
    }

    @FXML
    void onAñadir(ActionEvent event) {
        Object src = event.getSource();
        if (!(src instanceof Button))
            return;
        int idx = -1;
        if (addButtons != null)
            idx = addButtons.indexOf((Button) src);
        if (idx < 0)
            return;
        if (currentDisplayedProducts == null || idx >= currentDisplayedProducts.size())
            return;
        Product p = currentDisplayedProducts.get(idx);
        addProductToCart(p, 1);
    }

    @FXML
    void onAñadirExpress(ActionEvent event) {
        Object src = event.getSource();
        if (!(src instanceof Button))
            return;
        int idx = -1;
        if (expressButtons != null)
            idx = expressButtons.indexOf((Button) src);
        if (idx < 0)
            return;
        if (currentDisplayedProducts == null || idx >= currentDisplayedProducts.size())
            return;
        Product p = currentDisplayedProducts.get(idx);
        addProductToCart(p, 1);
        // also save as student preference (if logged in)
        try {
            String userEmail = AppState.getUserEmail();
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                PreferenciasDao pd = new PreferenciasDao(userEmail);
                pd.addPreference(p);
            }
        } catch (Exception e) {
            System.err.println("Error guardando preferencia: " + e.getMessage());
        }
    }

    @FXML
    void onBuscarAction(ActionEvent event) {
        if (txtBuscar != null) {
            performSearch(txtBuscar.getText());
        }
    }

    @FXML
    void onMostrarTodos(ActionEvent event) {
        if (allProducts != null) {
            displayProducts(allProducts);
        }
    }

    @FXML
    void onCategoria(ActionEvent event) {
        Object src = event.getSource();
        if (!(src instanceof Button))
            return;
        String cat = ((Button) src).getText();
        if (cat == null || cat.trim().isEmpty())
            return;
        List<Product> filtered = new ArrayList<>();
        if (allProducts != null) {
            for (Product p : allProducts) {
                if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(cat)) {
                    filtered.add(p);
                }
            }
        }
        displayProducts(filtered);
    }

    @FXML
    void onAtras(ActionEvent event) {
        Navigator.navigateTo("/views/tiendas_estudiante.fxml", "tiendas", true, event);
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

    /**
     * Añade un producto al carrito con notificación al usuario.
     */
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
                user,
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                CarritoManager.getInstancia().getItems().size());
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }
}
