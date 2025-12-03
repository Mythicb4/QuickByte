package com.mycompany.quickbite;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.service.SaleService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QBQrScannerFX implements Initializable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    private ImageView imgCamera;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmar;

    private Webcam webcam;
    private AnimationTimer timer;
    private ExecutorService executor;
    private volatile boolean isScanning = true;
    private Sale scannedSale;
    private SaleService saleService;
    private ProductService productService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Inicializar servicios
            String businessEmail = SessionContext.getLoggedInBusinessEmail();
            SaleDao saleDao = new SaleDao(businessEmail);
            ProductDao productDao = new ProductDao(businessEmail);
            this.productService = new ProductService(productDao);
            this.saleService = new SaleService(saleDao, productService);

            // Inicializar webcam
            executor = Executors.newSingleThreadExecutor();
            initializeWebcam();
            startScanning();
        } catch (Exception e) {
            showError("Error al inicializar la cámara: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeWebcam() {
        try {
            System.out.println("Buscando cámaras disponibles...");

            // Listar todas las cámaras disponibles
            var webcams = Webcam.getWebcams();
            System.out.println("Cámaras encontradas: " + webcams.size());
            for (int i = 0; i < webcams.size(); i++) {
                System.out.println("Cámara " + i + ": " + webcams.get(i).getName());
            }

            webcam = Webcam.getDefault();
            if (webcam == null) {
                Platform.runLater(() -> {
                    showError("No se encontró ninguna cámara disponible.\n\n" +
                            "Verifica que:\n" +
                            "1. La cámara esté conectada\n" +
                            "2. No esté siendo usada por otra aplicación\n" +
                            "3. Windows tenga permisos para acceder a la cámara");
                });
                return;
            }

            System.out.println("Usando cámara: " + webcam.getName());
            System.out.println("Abriendo cámara...");

            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();

            System.out.println("Cámara abierta exitosamente: " + webcam.isOpen());
            Platform.runLater(() -> lblStatus.setText("🔍 Buscando código QR..."));

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                showError("Error al abrir la cámara: " + e.getMessage() +
                        "\n\nVerifica que no esté siendo usada por otra aplicación.");
            });
        }
    }

    private void startScanning() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (webcam != null && webcam.isOpen() && isScanning) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        // Mostrar imagen en el ImageView
                        WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
                        imgCamera.setImage(fxImage);

                        // Intentar decodificar QR en un hilo separado
                        executor.submit(() -> decodeQR(image));
                    }
                }
            }
        };
        timer.start();
    }

    private void decodeQR(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);

            if (result != null) {
                String qrData = result.getText();
                Platform.runLater(() -> processQRData(qrData));
            }
        } catch (NotFoundException e) {
            // No se encontró QR, continuar escaneando
        } catch (Exception e) {
            Platform.runLater(() -> {
                System.err.println("Error decodificando QR: " + e.getMessage());
            });
        }
    }

    private void processQRData(String qrData) {
        try {
            isScanning = false; // Detener el escaneo

            // Parsear el formato compacto:
            // U:email|B:negocio|I:prod,cant,precio;prod2,cant2,precio2|T:total
            Sale sale = parseCompactQR(qrData);

            if (sale != null) {
                scannedSale = sale;
                lblStatus.setText("✅ QR Detectado - Cliente: " + sale.getClientName());
                lblStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                btnConfirmar.setDisable(false);
            } else {
                lblStatus.setText("❌ QR Inválido - Intente nuevamente");
                lblStatus.setStyle("-fx-text-fill: red;");
                isScanning = true; // Continuar escaneando
            }

        } catch (Exception e) {
            lblStatus.setText("❌ Error al procesar QR - Intente nuevamente");
            lblStatus.setStyle("-fx-text-fill: red;");
            isScanning = true; // Continuar escaneando
            System.err.println("Error procesando QR: " + e.getMessage());
        }
    }

    private Sale parseCompactQR(String qrData) {
        try {
            // Formato: U:email|B:negocio|I:prod,cant,precio;prod2,cant2,precio2|T:total
            String[] parts = qrData.split("\\|");

            String clientEmail = "";
            String businessEmail = "";
            double total = 0.0;
            List<SaleItem> items = new ArrayList<>();

            for (String part : parts) {
                if (part.startsWith("U:")) {
                    clientEmail = part.substring(2);
                } else if (part.startsWith("B:")) {
                    businessEmail = part.substring(2);
                } else if (part.startsWith("T:")) {
                    total = Double.parseDouble(part.substring(2));
                } else if (part.startsWith("I:")) {
                    String itemsData = part.substring(2);
                    String[] itemList = itemsData.split(";");

                    for (String itemStr : itemList) {
                        String[] itemParts = itemStr.split(",");
                        if (itemParts.length == 3) {
                            String productId = itemParts[0];
                            int quantity = Integer.parseInt(itemParts[1]);
                            double price = Double.parseDouble(itemParts[2]);

                            // Obtener el nombre del producto usando ProductService
                            Product product = productService.getProductByIdSafe(productId);
                            String productName = product != null ? product.getName() : "Desconocido";

                            SaleItem item = new SaleItem(productId, productName, quantity, price);
                            items.add(item);
                        }
                    }
                }
            }

            // Crear Sale
            Sale sale = new Sale();
            sale.setId(java.util.UUID.randomUUID().toString());
            sale.setBusinessEmail(businessEmail);
            sale.setClientName(clientEmail);
            sale.setPaymentMethod("QR");
            sale.setTotalAmount(total);
            sale.setReceivedAmount(total);
            sale.setChange(0.0);
            sale.setItems(items);
            sale.setSaleDate(LocalDateTime.now());

            return sale;

        } catch (Exception e) {
            System.err.println("Error parseando QR compacto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        if (scannedSale == null) {
            showError("No hay ningún pedido escaneado.");
            return;
        }

        try {
            // Guardar la venta en AppState para que la vista de ventas la cargue
            com.mycompany.quickbite.util.AppState.setScannedSaleFromQR(scannedSale);

            // Cerrar recursos de la cámara
            cleanup();

            // Navegar a la vista de ventas con los datos prellenados (sin cerrar ventana
            // anterior)
            Navigator.navigateTo("/views/ventas_negocio.fxml", "Registrar Venta", false, event);

        } catch (Exception e) {
            showError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        cleanup();
        Navigator.navigateTo("/views/login_negocio.fxml", "Negocio Dashboard", false, event);
    }

    private void cleanup() {
        isScanning = false;

        if (timer != null) {
            timer.stop();
        }

        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }

        if (executor != null) {
            executor.shutdown();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
