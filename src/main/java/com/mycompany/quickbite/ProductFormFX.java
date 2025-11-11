package com.mycompany.quickbite;

import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProductFormFX {

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtCategory;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtStock;
    @FXML private TextField txtCost;
    @FXML private TextField txtSupplier;
    @FXML private TextField txtImagePath;

    private ProductService productService;
    private Product editingProduct; // null si es nuevo

    public void setService(ProductService service) {
        this.productService = service;
    }

    public void setProduct(Product product) {
        this.editingProduct = product;
        if (product != null) {
            txtName.setText(product.getName());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtCategory.setText(product.getCategory());
            txtDescription.setText(product.getDescription());
            txtStock.setText(String.valueOf(product.getStock()));
            txtCost.setText(String.valueOf(product.getCost()));
            txtSupplier.setText(product.getSupplier());
            txtImagePath.setText(product.getImagePath());
        }
    }

    @FXML
    private void onSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            txtImagePath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onSave() {
        try {
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            String category = txtCategory.getText();
            String description = txtDescription.getText();
            int stock = Integer.parseInt(txtStock.getText());
            double cost = Double.parseDouble(txtCost.getText());
            String supplier = txtSupplier.getText();
            String imagePath = txtImagePath.getText();

            if (editingProduct == null) {
                Product newProduct = new Product(name, price, category, description, stock, imagePath, cost, supplier, 5);
                productService.createProduct(newProduct);
            } else {
                editingProduct.setName(name);
                editingProduct.setPrice(price);
                editingProduct.setCategory(category);
                editingProduct.setDescription(description);
                editingProduct.setStock(stock);
                editingProduct.setCost(cost);
                editingProduct.setSupplier(supplier);
                editingProduct.setImagePath(imagePath);
                productService.updateProduct(editingProduct);
            }

            closeWindow();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}
