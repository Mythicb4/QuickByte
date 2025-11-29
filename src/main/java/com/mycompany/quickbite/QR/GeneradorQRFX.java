/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickbite.QR;
/**
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
*/
import javafx.scene.image.Image;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author USUARIO CAB
 */
public class GeneradorQRFX {
    public Image generarQR(String pedido, int size)throws Exception{
        QRCodeWriter qrcodeWriter = new QRCodeWriter();
        BitMatrix matriz = qrcodeWriter.encode(
            pedido,
            BarcodeFormat.QR_CODE,
            size,
            size        
        );
        WritableImage qrImage = new WritableImage(size,size);
        PixelWriter pixelWriter = qrImage.getPixelWriter();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                
                // Si matrix.get(x, y) es TRUE, el píxel es un módulo oscuro
                if (matriz.get(x, y)) {
                    pixelWriter.setColor(x, y, Color.BLACK); // Usar Color de JavaFX
                } else {
                    pixelWriter.setColor(x, y, Color.WHITE); // Usar Color de JavaFX
                }
            }
        }
        return qrImage;
    }  
}