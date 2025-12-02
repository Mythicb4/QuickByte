package com.mycompany.quickbite;

import com.mycompany.quickbite.QR.GeneradorQRFX;
import com.mycompany.quickbite.dao.StudentPurchaseDao;
import com.mycompany.quickbite.model.Business;
import com.mycompany.quickbite.model.PurchaseItem;
import com.mycompany.quickbite.model.StudentPurchase;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.CarritoManager;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SerializadorPedido;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QBQrFX implements Initializable {

    @FXML
    private Button btnCancelar;

    @FXML
    private ImageView imgQr;

    @FXML
    private Label lblName;

    @FXML
    private Label lblProductos;

    @FXML
    private Label lblTotal;

    private final GeneradorQRFX generador = new GeneradorQRFX();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Obtener el negocio seleccionado
            Business selectedBusiness = AppState.getSelectedBusiness();
            String businessName = "Cafetería";
            String businessLocation = "";

            if (selectedBusiness != null) {
                businessName = selectedBusiness.getBusinessName();
                businessLocation = selectedBusiness.getLocation();
                lblName.setText(businessName + " - " + businessLocation);
            } else {
                lblName.setText(businessName);
            }

            // Obtener lista de productos del carrito
            String productos = CarritoManager.getInstancia().getItems().stream()
                    .map(item -> item.getQuantity() + "x " + item.getProductName())
                    .collect(Collectors.joining(", "));
            lblProductos.setText("Pedido: " + productos);

            // Calcular total
            double total = CarritoManager.getInstancia().calcularTotal();
            lblTotal.setText(String.format("Total: $ %.2f", total));

            // Generar QR
            String pedidoSerializado = SerializadorPedido.serializar();
            if (pedidoSerializado != null && !pedidoSerializado.isEmpty()) {
                final int TAMAÑO_QR = 300;
                Image qrImage = generador.generarQR(pedidoSerializado, TAMAÑO_QR);
                imgQr.setImage(qrImage);

                // ====== GUARDAR LA COMPRA EN EL HISTORIAL ======
                savePurchaseToHistory(businessName, businessLocation, total);

            } else {
                new Alert(Alert.AlertType.ERROR, "Error al serializar el pedido.").showAndWait();
            }

        } catch (Exception e) {
            System.err.println("Error al generar QR: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al generar el código QR: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Guarda la compra actual en el historial del estudiante
     */
    private void savePurchaseToHistory(String businessName, String businessLocation, double total) {
        try {
            String studentEmail = AppState.getUserEmail();
            if (studentEmail == null || studentEmail.isEmpty()) {
                System.err.println("No se pudo guardar la compra: email de estudiante no disponible");
                return;
            }

            // Convertir items del carrito a PurchaseItems
            List<PurchaseItem> purchaseItems = CarritoManager.getInstancia().getItems().stream()
                    .map(item -> new PurchaseItem(
                            item.getProductName(),
                            item.getQuantity(),
                            item.getPrice()))
                    .collect(Collectors.toList());

            // Crear la compra
            StudentPurchase purchase = new StudentPurchase();
            purchase.setStudentEmail(studentEmail);
            purchase.setBusinessName(businessName);
            purchase.setBusinessLocation(businessLocation);
            purchase.setPurchaseDate(LocalDateTime.now());
            purchase.setTotalAmount(total);
            purchase.setPaymentMethod("QR");
            purchase.setItems(purchaseItems);

            // Guardar en el DAO
            StudentPurchaseDao dao = new StudentPurchaseDao(studentEmail);
            dao.addPurchase(purchase);

            System.out.println("Compra guardada exitosamente: ID=" + purchase.getId());

        } catch (Exception e) {
            System.err.println("Error guardando compra en historial: " + e.getMessage());
            e.printStackTrace();
            // No mostramos alert para no interrumpir la experiencia del usuario
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        // Vaciar el carrito después de mostrar el QR
        CarritoManager.getInstancia().vaciarCarrito();
        // Volver al dashboard del estudiante
        Navigator.navigateTo("/views/login_estudiante.fxml", "QuickBite - Dashboard", true, event);
    }

}
