package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class QBProductFX implements Initializable {

    // --- Dependencias y Datos ---
    private ProductService productService;
    private ObservableList<Product> productList;

    // --- Elementos FXML (Tabla y Columnas) ---
    @FXML private TableView<Product> tblProductos;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Double> colCost;
    @FXML private TableColumn<Product, String> colProvider;
    @FXML private TableColumn<Product, String> colDescription;
    @FXML private TableColumn<Product, String> colImage; 
    
    @FXML private Button btnAtras; 
    // Los botones btnAdd, btnEdit, btnRemove y btnSearch también están presentes en el FXML y se usan por el onAction

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // 1. Obtener el email del negocio logueado
            String businessEmail = SessionContext.getLoggedInBusinessEmail();
            
            // 2. Crear el DAO específico para este negocio
            ProductDao productDao = new ProductDao(businessEmail); 
            
            // 3. Inicializar el servicio
            this.productService = new ProductService(productDao);
            
            // 4. Inicialización de columnas (el resto es igual)
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price")); 
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category")); 
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock")); 
            colCost.setCellValueFactory(new PropertyValueFactory<>("cost")); 
            colProvider.setCellValueFactory(new PropertyValueFactory<>("supplier")); 
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description")); 
            colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
            
            loadProducts();
            
        } catch (IllegalStateException e) {
            // Manejar el caso de que el email no se haya establecido (ej. error de login)
            showError("Error de Sesión", "No se encontró el email del negocio. Regrese al login.");
            // Opcional: navegar de vuelta al login
            // Navigator.navigateTo("/views/login_negocio.fxml", "Login", null); 
        } catch (Exception e) {
            showError("Error de Inicialización", "No se pudo inicializar la gestión de productos: " + e.getMessage());
        }
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productList = FXCollections.observableArrayList(products);
            tblProductos.setItems(productList);
            tblProductos.refresh(); 
        } catch (Exception e) {
            showError("Error de Carga", "No se pudo cargar la lista de productos: " + e.getMessage());
        }
    }

    // --- Handlers de Botones ---

    @FXML
    private void onAdd() {
        showProductForm(null);
    }

    @FXML
    private void onEdit() {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Advertencia", "Seleccione un producto para modificar.");
            return;
        }
        showProductForm(selected);
    }
    
    @FXML
    private void onRemove() {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Advertencia", "Seleccione un producto para eliminar.");
            return;
        }

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar el producto: " + selected.getName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                productService.deleteProduct(selected.getId());
                showInfo("Éxito", "Producto eliminado correctamente.");
                loadProducts(); 
            } catch (Exception e) {
                showError("Error de Eliminación", "No se pudo eliminar el producto: " + e.getMessage());
            }
        }
    }

    @FXML 
    // *** Este método ahora implementa la funcionalidad de "Consultar" ***
    private void onSearch(ActionEvent event) {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Advertencia", "Seleccione un producto para consultar.");
            return;
        }
        
        String info = String.format(
            """
            ID: %s
            Nombre: %s
            Precio: %.2f
            Categoría: %s
            Stock: %d
            Costo: %.2f
            Proveedor: %s
            Descripción: %s
            Ruta Imagen: %s
            """,
            selected.getId(),
            selected.getName(),
            selected.getPrice(),
            selected.getCategory(),
            selected.getStock(),
            selected.getCost(),
            selected.getSupplier(),
            selected.getDescription(),
            selected.getImagePath()
        );

        showInfo("Detalles del producto", info);
    }
    
    @FXML 
    private void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }
    
    // --- Lógica de Formulario ---
    
    private void showProductForm(Product productToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/product_form.fxml"));
            Parent root = loader.load();

            ProductFormFX controller = loader.getController();
            controller.setService(productService);
            controller.setProduct(productToEdit); 

            Stage stage = new Stage();
            stage.setTitle(productToEdit == null ? "Crear Nuevo Producto" : "Modificar Producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); 
            
            loadProducts(); // Recarga la tabla después de que la ventana de edición se cierra

        } catch (IOException e) {
            showError("Error de UI", "No se pudo cargar el formulario de producto: " + e.getMessage());
        }
    }
    
    // --- Métodos de Ayuda para Alertas ---

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}