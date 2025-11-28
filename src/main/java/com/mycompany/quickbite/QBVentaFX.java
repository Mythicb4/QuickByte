package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.SaleItem; // ⬅️ NUEVA CLASE
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext; // ⬅️ ASUMIMOS SU EXISTENCIA
import javafx.collections.FXCollections;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // ⬅️ INTERFAZ CLAVE
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import java.util.Arrays;
import java.util.List;

// 1. Implementa Initializable
public class QBVentaFX implements Initializable { 
    
    // --- Dependencias y Estado ---
    private ProductService productService;
    private final ObservableList<SaleItem> saleItems = FXCollections.observableArrayList(); // Lista para la tabla
    
    // --- Campos FXML (Actualizados según el FXML que te proporcioné) ---

    // Campos de la Venta
    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> cmdMetodo;
    @FXML private TextField txtIngreso;
    @FXML private TextField txtTotal;
    @FXML private Label lblChange;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    // Componentes de Añadir Producto
    @FXML private ComboBox<Product> cmbProduct; // ⬅️ ComboBox para Productos
    @FXML private TextField txtQuantity;
    @FXML private Button btnAddProduct;
    
    // Tabla y Columnas
    @FXML private TableView<SaleItem> tblSaleItems;
    @FXML private TableColumn<SaleItem, String> colProductName;
    @FXML private TableColumn<SaleItem, Integer> colQuantity;
    @FXML private TableColumn<SaleItem, Double> colUnitPrice;
    @FXML private TableColumn<SaleItem, Double> colSubtotal;

    // 2. Método initialize para cargar datos al iniciar
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // 2.1. Conectar las columnas de la tabla con el modelo SaleItem
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblSaleItems.setItems(saleItems);
        
        // Inicializa el ComboBox de Métodos de Pago
        cmdMetodo.setItems(FXCollections.observableArrayList(
            "Efectivo", 
            "Transferencia", 
            "Tarjeta"
        ));

        // Seleccionar el primer elemento por defecto
        cmdMetodo.getSelectionModel().selectFirst();

        // 2.2. Obtener el email del negocio logueado
        String businessEmail = SessionContext.getLoggedInBusinessEmail();
        
        if (businessEmail != null) {
            // 2.3. Inicializar el Service y DAO con el email del negocio
            ProductDao productDao = new ProductDao(businessEmail);
            this.productService = new ProductService(productDao);

            // 2.4. Cargar la lista de productos disponibles
            loadProductsIntoComboBox();
        } else {
            // Manejar error si no hay sesión iniciada (debería ser imposible si el flujo es correcto)
            System.err.println("Error: No se encontró el email del negocio en la sesión.");
        }
    }
    
    /**
     * Carga todos los productos disponibles del negocio en el ComboBox.
     */
    private void loadProductsIntoComboBox() {
        try {
            // Filtramos solo los productos disponibles (enabled=true y stock > 0)
            List<Product> availableProducts = productService.getAllProducts().stream()
                .filter(Product::isAvailable)
                .collect(Collectors.toList());
            
            // Usamos FXCollections para envolver la lista y cargarla en el ComboBox
            ObservableList<Product> products = FXCollections.observableArrayList(availableProducts);
            cmbProduct.setItems(products);
            
            // Opcional: Seleccionar el primer elemento por defecto
            if (!products.isEmpty()) {
                cmbProduct.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- Métodos de Acción FXML ---

    @FXML
    void onAddProduct(ActionEvent event) {
        // Implementación futura: Añadir el producto seleccionado a la tabla saleItems
        System.out.println("Producto a añadir: " + cmbProduct.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onCancel(ActionEvent event) {
        // Usamos el método que me pediste crear en Navigator.java
        Navigator.closeStage(event);
    }

    @FXML
    void onSave(ActionEvent event) {
        // Implementación futura: Lógica para guardar la venta
    }
}