package com.mycompany.quickbite.QR;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
public class ControlGenerador {

    @FXML
    private ImageView imageViewQR;
    
    public void setPedidoData(String pedidoString) {
        if (pedidoString != null && !pedidoString.isEmpty()) {
            handleGenerarQR(pedidoString);
        } else {
            // Manejo de error si el string está vacío (aunque ya se verifica en el controlador del estudiante)
            System.err.println("Datos del pedido vacíos en ControlGenerador.");
        }
    }
  
    private final GeneradorQRFX generador = new GeneradorQRFX();
  
    public void handleGenerarQR(String pedido){
        
        final int TAMAÑO_QR = 300;
        if (pedido.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "No se han pedido productos.").showAndWait();
            return;
        }
        try{
            Image qrImage = generador.generarQR(pedido, TAMAÑO_QR);
            
            imageViewQR.setImage(qrImage);
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Error al generar el QR: " + e.getMessage()).showAndWait();
        }
        
    }
    
    
    
}
