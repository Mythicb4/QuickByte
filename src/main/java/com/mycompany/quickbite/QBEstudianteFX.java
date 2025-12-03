package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.PreferenciasDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QBEstudianteFX implements Initializable {

    // -------------------------------------------------------------------------
    // --- VARIABLES DE GESTIÓN DEL PEDIDO ---
    // -------------------------------------------------------------------------

    // Nombre de usuario simulado (Debería venir del login)
    private final String currentUser = AppState.getUserEmail();

    // Variables para almacenar los productos preferidos y poder acceder a sus IDs
    private Product preferredProduct1;
    private Product preferredProduct2;
    private Product preferredProduct3;
    private Product preferredProduct4;

    // -------------------------------------------------------------------------
    // --- FXML FIELDS (ACTUALIZADO) ---
    // -------------------------------------------------------------------------

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
    private Button btnCarrito;
    @FXML
    private Button btnCompraExpress;
    @FXML
    private Button btnCompraExpress1;
    @FXML
    private Button btnCompraExpress2;
    @FXML
    private Button btnCompraExpress3;
    @FXML
    private Button btnHistorial;
    @FXML
    private Button btnMore; // Preservado del archivo original
    @FXML
    private Button btnPerfil;
    @FXML
    private Button btnPreferencias;
    @FXML
    private Button btnSalir;
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
    private Label lblCNames;
    @FXML
    private Label lblLogo;
    @FXML
    private Label lblLogo1; // <--- CAMPO NUEVO AÑADIDO
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
    private Label lblNoPreferidos;

    // -------------------------------------------------------------------------
    // --- MÉTODOS DE LÓGICA DEL PEDIDO (PRESERVADOS) ---
    // -------------------------------------------------------------------------

    /**
     * Lógica común para añadir un producto al pedido/carrito.
     */
    private void addProductToOrder(String currentUser, String productId, String productName, double price,
            int quantity) {
        // Creamos el ítem del pedido
        OrdenarProducto nuevoProducto = new OrdenarProducto(currentUser, productId, productName, price, quantity);

        // Lo añadimos a la lista (carrito)
        CarritoManager.getInstancia().addItem(nuevoProducto);

        // NOTIFICACIÓN: Mostrar la información del pedido (Nombre Usuario, Producto,
        // Precio, Cantidad)
        String mensaje = String.format(
                "Producto añadido\n" +
                        "Usuario: %s\n" +
                        "Producto: %s\n" +
                        "Precio Unitario: $%.2f\n" +
                        "Cantidad: %d\n" +
                        "Total Actual del Carrito: %d ítems.",
                currentUser,
                nuevoProducto.getProductName(),
                nuevoProducto.getPrice(),
                nuevoProducto.getQuantity(),
                CarritoManager.getInstancia().getItems().size());
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }

    @FXML
    void handleProduct1Order(ActionEvent event) {
        if (preferredProduct1 != null) {
            addProductToOrder(currentUser, preferredProduct1.getId(), preferredProduct1.getName(),
                    preferredProduct1.getPrice(), 1);
        }
    }

    @FXML
    void handleProduct2Order(ActionEvent event) {
        if (preferredProduct2 != null) {
            addProductToOrder(currentUser, preferredProduct2.getId(), preferredProduct2.getName(),
                    preferredProduct2.getPrice(), 1);
        }
    }

    @FXML
    void handleProduct3Order(ActionEvent event) {
        if (preferredProduct3 != null) {
            addProductToOrder(currentUser, preferredProduct3.getId(), preferredProduct3.getName(),
                    preferredProduct3.getPrice(), 1);
        }
    }

    @FXML
    void handleProduct4Order(ActionEvent event) {
        if (preferredProduct4 != null) {
            addProductToOrder(currentUser, preferredProduct4.getId(), preferredProduct4.getName(),
                    preferredProduct4.getPrice(), 1);
        }
    }

    // -------------------------------------------------------------------------
    // --- MÉTODOS DE NAVEGACIÓN (Corrección de onBtnSingup) ---
    // -------------------------------------------------------------------------

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String user = AppState.getUserEmail();
            if (user == null || user.trim().isEmpty()) {
                // No user; hide pref section
                if (HBCompraExpress != null) {
                    HBCompraExpress.setVisible(false);
                    HBCompraExpress.setManaged(false);
                }
                if (HBCompraExpress1 != null) {
                    HBCompraExpress1.setVisible(false);
                    HBCompraExpress1.setManaged(false);
                }
                if (lblNoPreferidos != null) {
                    lblNoPreferidos.setVisible(true);
                    lblNoPreferidos.setManaged(true);
                }
                return;
            }

            PreferenciasDao pd = new PreferenciasDao(user);
            List<Product> prefs = pd.loadAll();
            boolean empty = prefs == null || prefs.isEmpty();

            if (empty) {
                if (HBCompraExpress != null) {
                    HBCompraExpress.setVisible(false);
                    HBCompraExpress.setManaged(false);
                }
                if (HBCompraExpress1 != null) {
                    HBCompraExpress1.setVisible(false);
                    HBCompraExpress1.setManaged(false);
                }
                if (lblNoPreferidos != null) {
                    lblNoPreferidos.setVisible(true);
                    lblNoPreferidos.setManaged(true);
                }
                // Also hide individual slots
                if (SPProduct != null) {
                    SPProduct.setVisible(false);
                    SPProduct.setManaged(false);
                }
                if (SPProduct1 != null) {
                    SPProduct1.setVisible(false);
                    SPProduct1.setManaged(false);
                }
                if (SPProduct2 != null) {
                    SPProduct2.setVisible(false);
                    SPProduct2.setManaged(false);
                }
                if (SPProduct3 != null) {
                    SPProduct3.setVisible(false);
                    SPProduct3.setManaged(false);
                }
            } else {
                if (lblNoPreferidos != null) {
                    lblNoPreferidos.setVisible(false);
                    lblNoPreferidos.setManaged(false);
                }
                if (HBCompraExpress != null) {
                    HBCompraExpress.setVisible(true);
                    HBCompraExpress.setManaged(true);
                }
                if (HBCompraExpress1 != null) {
                    HBCompraExpress1.setVisible(true);
                    HBCompraExpress1.setManaged(true);
                }

                // Populate up to 4 slots with actual preferences; hide unused slots
                int size = Math.min(prefs.size(), 4);

                // slot 0
                if (SPProduct != null) {
                    if (size >= 1) {
                        Product p = prefs.get(0);
                        preferredProduct1 = p;
                        lblPName.setText(p.getName() != null ? p.getName() : "");
                        lblPPrice.setText(String.format("$ %.0f", p.getPrice()));
                        loadProductImage(p.getImagePath(), imgProduct);
                        SPProduct.setVisible(true);
                        SPProduct.setManaged(true);
                    } else {
                        SPProduct.setVisible(false);
                        SPProduct.setManaged(false);
                    }
                }

                // slot 1
                if (SPProduct1 != null) {
                    if (size >= 2) {
                        Product p = prefs.get(1);
                        preferredProduct2 = p;
                        lblPName1.setText(p.getName() != null ? p.getName() : "");
                        lblPPrice1.setText(String.format("$ %.0f", p.getPrice()));
                        loadProductImage(p.getImagePath(), imgProduct1);
                        SPProduct1.setVisible(true);
                        SPProduct1.setManaged(true);
                    } else {
                        SPProduct1.setVisible(false);
                        SPProduct1.setManaged(false);
                    }
                }

                // slot 2
                if (SPProduct2 != null) {
                    if (size >= 3) {
                        Product p = prefs.get(2);
                        preferredProduct3 = p;
                        lblPName2.setText(p.getName() != null ? p.getName() : "");
                        lblPPrice2.setText(String.format("$ %.0f", p.getPrice()));
                        loadProductImage(p.getImagePath(), imgProduct2);
                        SPProduct2.setVisible(true);
                        SPProduct2.setManaged(true);
                    } else {
                        SPProduct2.setVisible(false);
                        SPProduct2.setManaged(false);
                    }
                }

                // slot 3
                if (SPProduct3 != null) {
                    if (size >= 4) {
                        Product p = prefs.get(3);
                        preferredProduct4 = p;
                        lblPName3.setText(p.getName() != null ? p.getName() : "");
                        lblPPrice3.setText(String.format("$ %.0f", p.getPrice()));
                        loadProductImage(p.getImagePath(), imgProduct3);
                        SPProduct3.setVisible(true);
                        SPProduct3.setManaged(true);
                    } else {
                        SPProduct3.setVisible(false);
                        SPProduct3.setManaged(false);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error comprobando preferencias: " + e.getMessage());
        }
    }

    @FXML
    void onTienda(ActionEvent event) {
        Navigator.navigateTo("/views/tiendas_estudiante.fxml", "tiendas", true, event);
    }

    /**
     * Carga una imagen de producto manejando URLs HTTP/HTTPS y rutas de archivos
     * locales
     */
    private void loadProductImage(String imagePath, ImageView imageView) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return;
            }

            String imageUrl = imagePath.trim();

            // Si es HTTP o HTTPS, usar directamente
            if (imageUrl.toLowerCase().startsWith("http://") ||
                    imageUrl.toLowerCase().startsWith("https://")) {
                imageView.setImage(new Image(imageUrl, true));
            }
            // Si ya tiene el esquema file:, usar directamente
            else if (imageUrl.toLowerCase().startsWith("file:")) {
                imageView.setImage(new Image(imageUrl, true));
            }
            // Si es una ruta de Windows (C:\, D:\, etc.) o Unix (/)
            else if (imageUrl.matches("^[A-Za-z]:\\\\.*") || imageUrl.startsWith("/")) {
                // Normalizar la ruta de Windows
                String normalizedPath = imageUrl.replace("\\", "/");
                if (!normalizedPath.startsWith("/")) {
                    normalizedPath = "/" + normalizedPath;
                }
                imageView.setImage(new Image("file://" + normalizedPath, true));
            }
            // Asumir que es una ruta relativa a resources
            else {
                imageView.setImage(new Image(getClass().getResource(imageUrl).toExternalForm(), true));
            }
        } catch (Exception ex) {
            System.err.println("No se pudo cargar imagen: " + imagePath + " - " + ex.getMessage());
        }
    }
}