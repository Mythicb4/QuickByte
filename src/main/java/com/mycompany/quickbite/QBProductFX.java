package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.util.Navigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QBProductFX implements Initializable {

    @FXML private TableView<Product> tblProductos;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Double> colCost;
    @FXML private TableColumn<Product, String> ColProvider;
    @FXML private TableColumn<Product, String> colDescription;
    @FXML private TableColumn<Product, String> colImage;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnSearch;
    @FXML private Button btnRemove;
    @FXML private Button btnAtras;

    private ProductService productService;
    private ObservableList<Product> productList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productService = new ProductService(new ProductDao("data/products.json"));

        // Configurar columnas
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        ColProvider.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        // Cargar datos iniciales
        loadProducts();
    }

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();
        productList = FXCollections.observableArrayList(products);
        tblProductos.setItems(productList);
    }

    @FXML
    private void onRemove(ActionEvent event) {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un producto", "Debe seleccionar un producto para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Seguro que desea eliminar el producto?");
        confirm.setContentText(selected.getName());
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            productService.deleteProduct(selected.getId());
            loadProducts();
            showInfo("Producto eliminado", "Se eliminó correctamente.");
        }
    }

    @FXML
    private void onAdd(ActionEvent event) {
        openForm(null); // null → producto nuevo
    }

    @FXML
    private void onEdit(ActionEvent event) {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un producto", "Debe seleccionar un producto para modificar.");
            return;
        }
        openForm(selected);
    }

    private void openForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/product_form.fxml"));
            Parent root = loader.load();

            ProductFormFX controller = loader.getController();
            controller.setService(productService);
            controller.setProduct(product);

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Nuevo producto" : "Editar producto");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadProducts();
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }


    @FXML
    private void onSearch(ActionEvent event) {
        Product selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un producto", "Debe seleccionar un producto para consultar.");
            return;
        }

        String info = String.format("""
            ID: %s
            Nombre: %s
            Precio: %.2f
            Categoría: %s
            Stock: %d
            Costo: %.2f
            Proveedor: %s
            Descripción: %s
            """,
            selected.getId(),
            selected.getName(),
            selected.getPrice(),
            selected.getCategory(),
            selected.getStock(),
            selected.getCost(),
            selected.getSupplier(),
            selected.getDescription()
        );

        showInfo("Detalles del producto", info);
    }

    @FXML
    private void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", event);
    }

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
