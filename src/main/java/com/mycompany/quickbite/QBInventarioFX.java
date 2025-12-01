package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell; // Necesario para edición de texto/número
import javafx.util.converter.IntegerStringConverter; // Convierte String a Integer

import java.net.URL;
import java.util.ResourceBundle;
import java.util.NoSuchElementException;

public class QBInventarioFX implements Initializable {

    // --- Dependencias y Datos ---
    private ProductService productService;
    private ObservableList<Product> productList;

    // --- Elementos FXML (Tabla y Columnas) - IDs NO CAMBIADOS ---
    @FXML private TableView<Product> tblProductos; 
    @FXML private TableColumn<Product, String> colName; 
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colProvider;
    @FXML private TableColumn<Product, String> colEstado;
    // La columna editable para el Stock Mínimo
    @FXML private TableColumn<Product, Integer> colMinStock; 
    
    // --- Otros botones (sidebar) ya están en tu esqueleto... ---
    @FXML private Button btnAtras;
    @FXML private Button btnClientes;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnFacturacion;
    @FXML private Button btnInventario;
    @FXML private Button btnProduct;
    @FXML private Button btnQr;
    @FXML private Button btnReportes;
    @FXML private Button btnVentas;

    // --- Implementación del Inicializador ---

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Inicializar Servicio
        String businessEmail = SessionContext.getLoggedInBusinessEmail();
        this.productService = new ProductService(new ProductDao(businessEmail)); 

        // 2. Configurar Columnas
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProvider.setCellValueFactory(new PropertyValueFactory<>("supplier")); 
        colMinStock.setCellValueFactory(new PropertyValueFactory<>("minStock")); 

        setupStatusColumn(); 

        // 3. Cargar datos
        loadInventory();

        // 4. Habilitar Edición de Stock Mínimo
        setupMinStockEditing();
    }

    private void setupStatusColumn() {
        // 1. Definir el valor de la celda (el texto)
        colEstado.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            if (product != null && product.isLowStock()) { // true si stock <= minStock
                return new SimpleStringProperty("Stock Bajo");
            } else if (product != null) {
                return new SimpleStringProperty("Bien");
            } else {
                return new SimpleStringProperty("");
            }
        });

        // 2. Definir el aspecto de la celda (el color)
        colEstado.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Limpiar estilos y contenido anteriores
                getStyleClass().removeAll("status-bien", "status-bajo");

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);

                    // Asignar clase de estilo basado en el texto
                    if (item.equals("Stock Bajo")) {
                        getStyleClass().add("status-bajo"); // Usa el estilo para rojo
                    } else if (item.equals("Bien")) {
                        getStyleClass().add("status-bien"); // Usa el estilo para verde
                    }
                }
            }
        });
    }
    
    private void loadInventory() {
        try {
            // Cargar todos los productos (disponibles o no, para gestión de stock)
            productList = FXCollections.observableArrayList(productService.getAllProducts());
            tblProductos.setItems(productList);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Carga", "No se pudo cargar el inventario: " + e.getMessage());
        }
    }
    
    /**
     * Pinta las filas de la tabla de un color de alerta si el stock es bajo (RF-09).
     */
    private void applyLowStockStyle() {
        tblProductos.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                // Limpiar estilos anteriores
                getStyleClass().removeAll("low-stock-row", "critical-stock-row"); 
                
                if (item != null && !empty) {
                    // Llama a isLowStock() del modelo
                    if (item.isLowStock()) { 
                        // Asumo que tienes una clase .low-stock-row en tu CSS
                        getStyleClass().add("low-stock-row");
                    }
                }
            }
        });
        // 🚨 NOTA: Recuerda agregar la regla CSS en el archivo styles/tu_estilo.css
        // Por ejemplo: .low-stock-row { -fx-background-color: #ffd6d6; }
    }

    /**
     * Permite editar la columna Stock Mínimo directamente en la tabla y guarda el cambio.
     */
    private void setupMinStockEditing() {
        // Usa IntegerStringConverter para permitir la edición de enteros
        colMinStock.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tblProductos.setEditable(true); // Habilitar edición de la tabla
        
        // Manejar el evento de commit de edición
        colMinStock.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            int newMinStock = event.getNewValue();

            if (newMinStock < 0) {
                 showAlert(AlertType.WARNING, "Alerta", "El stock mínimo no puede ser negativo.");
                 tblProductos.refresh(); // Vuelve al valor anterior
                 return;
            }
            
            try {
                // Actualiza el modelo y luego persiste
                product.setMinStock(newMinStock); 
                productService.updateProduct(product);
                showAlert(AlertType.INFORMATION, "Éxito", "Stock Mínimo de " + product.getName() + " actualizado a " + newMinStock);
                
                // Refrescar para re-aplicar el estilo de 'stock bajo' si es necesario
                tblProductos.refresh(); 
            } catch (NoSuchElementException e) {
                showAlert(AlertType.ERROR, "Error de Producto", "El producto no existe en la base de datos.");
                tblProductos.refresh();
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error al guardar", "No se pudo actualizar el stock mínimo: " + e.getMessage());
                tblProductos.refresh(); 
            }
        });
    }
    
    // --- Métodos de Navegación Existentes (Sidebar) ---
    
    @FXML
    void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }

    @FXML
    void onClientes(ActionEvent event) {
        Navigator.navigateTo("/views/cliente_negocio.fxml", "cliente", true, event);
    }

    @FXML
    void onConfiguracion(ActionEvent event) {
        Navigator.navigateTo("/views/configuracion_negocio.fxml", "configuracion", false, event);
    }

    @FXML
    void onFacturacion(ActionEvent event) {
        Navigator.navigateTo("/views/facturacion_negocio.fxml", "facturacion", true, event);
    }

    @FXML
    void onInventario(ActionEvent event) {
        // Ya estamos en Inventario
    }

    @FXML
    void onProduct(ActionEvent event) {
        Navigator.navigateTo("/views/producto_negocio.fxml", "producto", true, event);
    }

    @FXML
    void onQr(ActionEvent event) {
        // Agregar el link a qr para leer
    }
    
    @FXML
    void onReportes(ActionEvent event) {
        // Navegar a reportes
    }
    
    @FXML
    void onVentas(ActionEvent event) {
        // Navegar a ventas
    }
    
    // Método auxiliar de alerta
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}