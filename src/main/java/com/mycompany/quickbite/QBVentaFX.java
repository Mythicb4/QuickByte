package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.service.SaleService; // ⬅️ NUEVA IMPORTACIÓN
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QBVentaFX implements Initializable {

    // --- Dependencias y Estado ---
    private ProductService productService;
    private SaleDao saleDao;
    private SaleService saleService; // ⬅️ NUEVA DEPENDENCIA

    // Lista para la tabla, inicializada para los ítems de venta
    private final ObservableList<SaleItem> saleItems = FXCollections.observableArrayList();

    // Formato para mostrar precios y totales
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private double currentTotal = 0.0; // ⬅️ Acumulador del total de la venta

    // --- Campos FXML ---
    // Campos de la Venta
    @FXML
    private TextField txtCliente; // Asumimos que es para un nombre o ID de cliente (opcional)
    @FXML
    private ComboBox<String> cmdMetodo;
    @FXML
    private TextField txtReceived; // Dinero recibido
    @FXML
    private TextField txtTotal;
    @FXML
    private Label lblChange; // Cambio / Faltante
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    // Componentes de Añadir Producto
    @FXML
    private ComboBox<Product> cmbProduct;
    @FXML
    private TextField txtQuantity;
    @FXML
    private Button btnAddProduct;

    // Tabla y Columnas
    @FXML
    private TableView<SaleItem> tblSaleItems;
    @FXML
    private TableColumn<SaleItem, String> colProductName;
    @FXML
    private TableColumn<SaleItem, Integer> colQuantity;
    @FXML
    private TableColumn<SaleItem, Double> colUnitPrice;
    @FXML
    private TableColumn<SaleItem, Double> colSubtotal;

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
                "Tarjeta"));

        // Seleccionar el primer elemento por defecto
        cmdMetodo.getSelectionModel().selectFirst();

        // Inicializar display de totales
        txtTotal.setText(currencyFormat.format(0.00));
        lblChange.setText(currencyFormat.format(0.00));

        // 2.2. Obtener el email del negocio logueado
        String businessEmail = SessionContext.getLoggedInBusinessEmail();

        if (businessEmail != null) {
            // 2.3. Inicializar el Service y DAO con el email del negocio
            ProductDao productDao = new ProductDao(businessEmail);
            this.productService = new ProductService(productDao);
            this.saleDao = new SaleDao(businessEmail);

            // ⬅️ INICIALIZAR EL SERVICIO DE VENTA
            this.saleService = new SaleService(this.saleDao, this.productService);

            // 2.4. Cargar la lista de productos disponibles
            loadProductsIntoComboBox();

            // 2.5. Listener para actualizar el cambio cada vez que txtReceived cambia
            txtReceived.textProperty().addListener((obs, oldV, newV) -> updateChange());

            // 2.6. Cargar datos del QR si existen
            loadScannedSaleFromQR();
        } else {
            // Manejar error si no hay sesión iniciada
            showAlert(AlertType.ERROR, "Error de Sesión", "No se encontró el email del negocio en la sesión.");
        }
    }

    /**
     * Carga los datos de una venta escaneada desde QR si existe en AppState
     */
    private void loadScannedSaleFromQR() {
        com.mycompany.quickbite.model.Sale scannedSale = com.mycompany.quickbite.util.AppState.getScannedSaleFromQR();

        if (scannedSale != null) {
            // Cargar datos en los campos
            txtCliente.setText(scannedSale.getClientName());
            cmdMetodo.setValue("QR");

            // Agregar items a la tabla
            if (scannedSale.getItems() != null) {
                for (com.mycompany.quickbite.model.SaleItem item : scannedSale.getItems()) {
                    saleItems.add(item);
                }
            }

            // Recalcular total
            recalculateTotal();

            // Pre-llenar el campo de dinero recibido con el total (sin formato, solo el
            // número)
            txtReceived.setText(String.format("%.2f", scannedSale.getTotalAmount()));

            // Limpiar el AppState para que no se recargue si vuelve a esta vista
            com.mycompany.quickbite.util.AppState.clearScannedSale();
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
            showAlert(AlertType.ERROR, "Error de Carga", "No se pudieron cargar los productos disponibles.");
        }
    }

    // --- Lógica de la Venta ---

    /**
     * Recalcula el total de la venta sumando los subtotales de todos los ítems.
     */
    private void recalculateTotal() {
        currentTotal = saleItems.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();

        txtTotal.setText(currencyFormat.format(currentTotal));
        updateChange(); // Actualiza el cambio inmediatamente
    }

    /**
     * Calcula y actualiza el cambio o faltante.
     */
    private void updateChange() {
        try {
            double received = Double.parseDouble(txtReceived.getText().replace(",", ".")); // Acepta comas/puntos
            double change = received - currentTotal;

            String changeText = currencyFormat.format(change);
            lblChange.setText(changeText);

            // Estilo para mostrar si es cambio (verde) o faltante (rojo)
            lblChange.setStyle(change >= 0 ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        } catch (NumberFormatException e) {
            // Si el campo no es un número válido
            lblChange.setText(currencyFormat.format(0.00));
            lblChange.setStyle("-fx-text-fill: black;");
        }
    }

    // --- Métodos de Acción FXML ---

    @FXML
    void onAddProduct(ActionEvent event) {
        Product selectedProduct = cmbProduct.getSelectionModel().getSelectedItem();
        int quantity;

        if (selectedProduct == null) {
            showAlert(AlertType.WARNING, "Advertencia", "Debe seleccionar un producto.");
            return;
        }

        try {
            quantity = Integer.parseInt(txtQuantity.getText());
            if (quantity <= 0) {
                showAlert(AlertType.WARNING, "Advertencia", "La cantidad debe ser un número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error de Cantidad", "La cantidad ingresada no es un número válido.");
            return;
        }

        if (quantity > selectedProduct.getStock()) {
            showAlert(AlertType.WARNING, "Stock Insuficiente",
                    String.format("Solo quedan %d unidades de %s en stock.",
                            selectedProduct.getStock(), selectedProduct.getName()));
            return;
        }

        // 1. Crear/Actualizar el SaleItem
        Optional<SaleItem> existingItem = saleItems.stream()
                .filter(item -> item.getProductId().equals(selectedProduct.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            SaleItem itemToUpdate = existingItem.get();
            int newQuantity = itemToUpdate.getQuantity() + quantity;

            if (newQuantity > selectedProduct.getStock()) {
                showAlert(AlertType.WARNING, "Stock Insuficiente",
                        String.format("Solo quedan %d unidades de %s en stock (ya tienes %d en la venta).",
                                selectedProduct.getStock(),
                                selectedProduct.getName(),
                                itemToUpdate.getQuantity()));
                return; // Detiene la adición si no hay suficiente stock
            }
            // Corrección 1: Usar el método que actualiza la propiedad de JavaFX
            itemToUpdate.setQuantity(newQuantity);

            // Esto obliga a la tabla a refrescar el ítem.
            tblSaleItems.refresh();
        } else {
            // Si no existe, lo añade
            // Corrección 2: Usar el constructor que requiere Product y cantidad (int)
            SaleItem newItem = new SaleItem(
                    selectedProduct, // ⬅️ Pasar el objeto Product completo
                    quantity);
            saleItems.add(newItem);
        }

        // 2. Recalcular el total y limpiar campos
        recalculateTotal();
        txtQuantity.clear();
        cmbProduct.getSelectionModel().clearSelection();
    }

    @FXML
    void onSave(ActionEvent event) {
        // 1. Validar que haya productos en la venta
        if (saleItems.isEmpty()) {
            showAlert(AlertType.WARNING, "Venta Vacía", "Debe agregar al menos un producto para guardar la venta.");
            return;
        }

        try {
            // CORRECCIÓN: Limpiar la cadena de formato de moneda antes de parsear.
            // 1. Eliminar el punto (separador de miles).
            // 2. Reemplazar la coma (separador decimal) por punto.

            String totalText = txtTotal.getText().replace(".", "").replace(",", ".");
            String receivedText = txtReceived.getText().replace(".", "").replace(",", ".");

            double total = Double.parseDouble(totalText);
            double received = Double.parseDouble(receivedText);

            // 2. Validar que el pago sea suficiente
            if (received < total) {
                showAlert(AlertType.ERROR, "Pago Insuficiente",
                        String.format("El monto recibido (%s) es menor al total de la venta (%s).",
                                currencyFormat.format(received), currencyFormat.format(total)));
                return;
            }

            // 3. Crear el objeto Sale (Modelo en inglés)
            Sale newSale = new Sale();
            newSale.setTotalAmount(total);
            newSale.setReceivedAmount(received);
            newSale.setChange(received - total);

            // Asignar el método de pago desde el ComboBox
            String paymentMethod = cmdMetodo.getValue();
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                newSale.setPaymentMethod(paymentMethod);
            } else {
                newSale.setPaymentMethod("Efectivo"); // Valor por defecto
            }

            // Manejo del nombre del Cliente desde el TextField
            String clientName = txtCliente.getText().trim();
            if (clientName.isEmpty()) {
                newSale.setClientName("Consumidor Final");
            } else {
                newSale.setClientName(clientName);
            }

            // Asignar los ítems de la venta
            newSale.setItems(saleItems.stream().collect(Collectors.toList()));

            // 4. **LLAMAR AL SERVICIO Y PERSISTIR (Descuenta Stock y Guarda Venta)**
            // El SaleService se encarga de llamar al ProductService y al SaleDao.
            Sale savedSale = saleService.registerSale(newSale); // ⬅️ Venta registrada y con ID.

            // 5. 🔑 MOSTRAR LA FACTURA Y PASAR DATOS (REEMPLAZANDO LA ALERTA DE ÉXITO)

            // Obtener el nombre del negocio (asumiendo que está en SessionContext)
            String businessName = SessionContext.getLoggedInBusinessEmail();
            String fxmlPath = "/views/factura_negocio.fxml";
            String title = "Comprobante de Venta - " + savedSale.getId();

            // Navegar a la nueva ventana, obtener el controlador de la factura
            // (QBFacturaFX)
            QBFacturaFX facturaController = Navigator.navigateToNewFloatingWindow(fxmlPath, title);

            if (facturaController != null) {
                // Llamar al método del controlador QBFacturaFX para inyectar los datos
                facturaController.setSaleData(savedSale, businessName);
            } else {
                // Mensaje de respaldo si la ventana de factura no se pudo abrir
                String successMessage = String.format(
                        "Venta guardada con éxito. ID: %s\nTotal: %s\nRecibido: %s\nCambio: %s. \nADVERTENCIA: No se pudo abrir la ventana de Factura.",
                        savedSale.getId(),
                        currencyFormat.format(savedSale.getTotalAmount()),
                        currencyFormat.format(savedSale.getReceivedAmount()),
                        currencyFormat.format(savedSale.getChange()));
                showAlert(AlertType.INFORMATION, "Éxito (con advertencia)", successMessage);
            }

            // 6. Mostrar mensaje de éxito
            showAlert(AlertType.INFORMATION, "Venta Registrada",
                    "✅ Venta registrada exitosamente.\n\nID: " + savedSale.getId());

            // 7. Cerrar la ventana de venta actual
            Navigator.closeStage(event);

            // 8. Regresar al dashboard del negocio
            Navigator.navigateTo("/views/login_negocio.fxml", "Negocio Dashboard", false, event);

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error de Datos",
                    "Verifique que el total y el dinero recibido sean números válidos. Asegúrese de no usar caracteres no numéricos.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error al guardar", "Ocurrió un error al procesar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        // Usamos el método que me pediste crear en Navigator.java
        Navigator.closeStage(event);
    }

    // --- Método de Utilidad ---
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}