/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickbite;

import com.mycompany.quickbite.util.CarritoManager;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SerializadorPedido;
import com.mycompany.quickbite.QR.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class QBcarritoFX implements Initializable {

    @FXML
    private TableView<OrdenarProducto> tablaCarrito;
    @FXML
    private Label lblTotalPagar;
    @FXML
    private Button btnGenerarQR;

    /**
     * Se llama automáticamente al cargar la escena.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Carga los items del CarritoManager a la TableView
        tablaCarrito.setItems(FXCollections.observableArrayList(CarritoManager.getInstancia().getItems()));
        
        // Calcula el total inicial
        actualizarTotal();
    }
    
    /**
     * Recalcula y actualiza el Label con el total a pagar.
     */
    private void actualizarTotal() {
        double total = CarritoManager.getInstancia().calcularTotal();
        lblTotalPagar.setText(String.format("$ %.2f", total));
    }

    /**
     * 1. Serializa el pedido.
     * 2. Navega a la escena del QR, inyectando el String serializado.
     */
    @FXML
    private void handleGenerarQR(ActionEvent event) {
        double total = CarritoManager.getInstancia().calcularTotal();
        
        if (total <= 0) {
            // Muestra una alerta si el carrito está vacío
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Carrito Vacío");
            alert.setHeaderText(null);
            alert.setContentText("No puedes generar un QR con el carrito vacío. Agrega productos.");
            alert.showAndWait();
            return;
        }
        
        try {
            // 1. Serializa el pedido
            String pedidoSerializado = SerializadorPedido.serializar();
            
            // 2. Carga la escena del generador (IMPORTANTE: USA FXMLLoader directo)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QR.fxml"));
            Parent root = loader.load();
            
            // 3. Obtiene el controlador del QR y le pasa el dato
            ControlGenerador controller = loader.getController();
            controller.handleGenerarQR(pedidoSerializado); // Llama al nuevo método
            
            // 4. Navega a la nueva escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("QuickBite - Código QR de Pedido");
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error al generar QR o navegar: " + e.getMessage());
            e.printStackTrace();
            // Mostrar error al usuario
            new Alert(Alert.AlertType.ERROR, "Error al procesar el pedido.").showAndWait();
        }
    }
    
    /**
     * Regresa a la vista de selección de productos.
     */
    @FXML
    private void handleRegresar(ActionEvent event) {
        Navigator.navigateTo("/views/login_estudiante.fxml", "QuickBite - Menú", true, event);
    }
}
