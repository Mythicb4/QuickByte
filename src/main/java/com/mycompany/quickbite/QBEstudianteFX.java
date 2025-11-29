package com.mycompany.quickbite;
import com.mycompany.quickbite.util.*;
import com.mycompany.quickbite.QR.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader; 
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.stage.Stage;
import java.io.IOException;

public class QBEstudianteFX {
    
    // -------------------------------------------------------------------------
    // --- VARIABLES DE GESTIÓN DEL PEDIDO ---
    // -------------------------------------------------------------------------
    
    // Lista para almacenar los ítems del carrito (Pedido)
    
    
    //private final ObservableList<OrdenarProducto> currentOrder = FXCollections.observableArrayList();
    
    // Nombre de usuario simulado (Debería venir del login)
    private final String currentUser = AppState.getUserEmail(); 

    // -------------------------------------------------------------------------
    // --- FXML FIELDS (SIN CAMBIOS) ---
    // -------------------------------------------------------------------------

    @FXML private HBox HBCompraExpress;
    @FXML private HBox HBCompraExpress1;
    @FXML private StackPane SPProduct;
    @FXML private StackPane SPProduct1;
    @FXML private StackPane SPProduct2;
    @FXML private StackPane SPProduct3;
    @FXML private Button btnCarrito;
    @FXML private Button btnCompraExpress;
    @FXML private Button btnCompraExpress1;
    @FXML private Button btnCompraExpress2;
    @FXML private Button btnCompraExpress3;
    @FXML private Button btnHistorial;
    @FXML private Button btnMore;
    @FXML private Button btnPerfil;
    @FXML private Button btnPreferencias;
    @FXML private Button btnSalir;
    @FXML private Button btnTienda;
    @FXML private ImageView imgLogo;
    @FXML private ImageView imgProduct;
    @FXML private ImageView imgProduct1;
    @FXML private ImageView imgProduct2;
    @FXML private ImageView imgProduct3;
    @FXML private Label lblCNames;
    @FXML private Label lblLogo;
    @FXML private Label lblPName;
    @FXML private Label lblPName1;
    @FXML private Label lblPName2;
    @FXML private Label lblPName3;
    @FXML private Label lblPPrice;
    @FXML private Label lblPPrice1;
    @FXML private Label lblPPrice2;
    @FXML private Label lblPPrice3;
    
    // -------------------------------------------------------------------------
    // --- MÉTODOS DE LÓGICA DEL PEDIDO ---
    // -------------------------------------------------------------------------
    
    /**
        Lógica común para añadir un producto al pedido/carrito.
     */
    private void addProductToOrder(String currentUser, String productName, double price, int quantity) {
        // Creamos el ítem del pedido
        OrdenarProducto nuevoProducto = new OrdenarProducto(currentUser, productName, price, quantity);
        
        // Lo añadimos a la lista (carrito)
        CarritoManager.getInstancia().addItem(nuevoProducto);
        
        // NOTIFICACIÓN: Mostrar la información del pedido (Nombre Usuario, Producto, Precio, Cantidad)
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
            CarritoManager.getInstancia().getItems().size()
        );
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }

    // --- HANDLERS ESPECÍFICOS PARA CADA BOTÓN DE PRODUCTO ---
    
    @FXML
    void handleNavigation(ActionEvent event) {
        Button source = (Button) event.getSource();
        String fxmlPath = "";
        String title = "";
        
        if (source == btnTienda) {
            // Asumiendo que esta es la vista de catálogo actual
            fxmlPath = "/views/login_estudiante.fxml"; 
            title = "Tienda QuickBite";
        } else if (source == btnPerfil) {
            fxmlPath = "/views/perfil_estudiante.fxml"; 
            title = "Mi Perfil";
        } else if (source == btnHistorial) {
            fxmlPath = "/views/historial_estudiante.fxml"; 
            title = "Historial de Pedidos";
        } else if (source == btnPreferencias) {
            fxmlPath = "/views/preferencias.fxml"; 
            title = "Preferencias";
        }
        
        if (!fxmlPath.isEmpty()) {
            Navigator.navigateTo(fxmlPath, title, event);
        }
    }
        
    @FXML
    void handleProduct1Order(ActionEvent event) {
        // Datos estáticos del FXML: Croissant, $3500, Cantidad 1
        String name = lblPName.getText();
        
        // Eliminar el "$" y convertir el texto del precio a un número.
        // Asumiendo que el formato es "$ 3500" o similar
        double price = Double.parseDouble(lblPPrice.getText().replace("$", "").trim()); 
        
        addProductToOrder(currentUser, name, price, 1);
    }
    
    @FXML
    void handleProduct2Order(ActionEvent event) {
        String name = lblPName1.getText();
        double price = Double.parseDouble(lblPPrice1.getText().replace("$", "").trim()); 
        addProductToOrder(currentUser, name, price, 1);
    }
    
    @FXML
    void handleProduct3Order(ActionEvent event) {
        String name = lblPName2.getText();
        double price = Double.parseDouble(lblPPrice2.getText().replace("$", "").trim()); 
        addProductToOrder(currentUser, name, price, 1);
    }
    
    @FXML
    void handleProduct4Order(ActionEvent event) {
        String name = lblPName3.getText();
        double price = Double.parseDouble(lblPPrice3.getText().replace("$", "").trim()); 
        addProductToOrder(currentUser, name, price, 1);
    }
    
    // -------------------------------------------------------------------------
    // --- MÉTODOS DE NAVEGACIÓN (Corrección de onBtnSingup) ---
    // -------------------------------------------------------------------------

    @FXML
    void handleLogout(ActionEvent event) {
        // Navegar al login principal
        CarritoManager.getInstancia().vaciarCarrito();
    }
    
    
    @FXML
    private void handleViewCartAndGenerateQR(ActionEvent event) {

        String pedidoString = SerializadorPedido.serializar();
        
        // 1. Verificar si hay productos después de usar SerializadorPedido (que usa CarritoManager)
        if (pedidoString == null || CarritoManager.getInstancia().getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El carrito está vacío. Agregue productos antes de generar el QR.").showAndWait();
            return;
        }
        Navigator.navigateTo("/views/carrito.fxml", "QuickBite - Mi Carrito", event);
    }
    
}
