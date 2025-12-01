package com.mycompany.quickbite;

import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.model.SaleItem;
import com.mycompany.quickbite.util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class QBFacturaFX implements Initializable {
    
    // Formateadores
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("###,##0.00");
    
    // FXML Inyecciones
    @FXML private Button btnCerrar;
    @FXML private Button btnImprimir;
    @FXML private Label lblCambio;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblId;
    @FXML private Label lblMetodo;
    @FXML private Label lblName;
    @FXML private Label lblRecibido;
    @FXML private Label lblTotal;
    
    @FXML private TableView<SaleItem> tblProductos;
    // Columnas para SaleItem
    @FXML private TableColumn<SaleItem, String> colProducto;
    @FXML private TableColumn<SaleItem, Integer> colCantidad;
    // Se usa Double para PrecioUnitario y Subtotal
    @FXML private TableColumn<SaleItem, Double> colPrecioUnitario; 
    @FXML private TableColumn<SaleItem, Double> colSubtotal; 
    
    private ObservableList<SaleItem> itemsData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
    }
    
    /**
     * Mapea las columnas de la vista con las propiedades del modelo SaleItem.
     * Los nombres de las propiedades deben coincidir **EXACTAMENTE** con los getters de SaleItem.
     */
    private void setupTable() {
        // Inicializar la lista observable
        itemsData = FXCollections.observableArrayList();
        tblProductos.setItems(itemsData);
        
        // Mapeo de columnas a las propiedades de SaleItem
        colProducto.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        
        // 🔑 PASO CLAVE: OCULTAR las columnas Cantidad y Subtotal
        colCantidad.setVisible(false);
        colSubtotal.setVisible(false);
        
        // Formatear Precio Unitario como moneda
        colPrecioUnitario.setCellFactory(this::createCurrencyCell);
    }
    
    /**
     * Método auxiliar para crear CellFactory para formatear valores Double como moneda.
     */
    private TableCell<SaleItem, Double> createCurrencyCell(TableColumn<SaleItem, Double> column) {
        return new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Formatear el valor a moneda
                    setText(CURRENCY_FORMAT.format(item));
                }
            }
        };
    }

    /**
     * Método público llamado desde QBFacturacionFX para inyectar los datos de la venta.
     * @param sale La venta seleccionada.
     * @param businessEmail El email del negocio (proporcionado desde SessionContext.getLoggedInBusinessEmail()).
     */
    public void setSaleData(Sale sale, String businessEmail) {
        if (sale == null) {
            lblName.setText("ERROR: No se pudo cargar la venta.");
            return;
        }

        // 1. Llenar los campos de la factura
        lblId.setText("ID de Venta: " + sale.getId());
        lblName.setText(businessEmail); 

        // 2. Formatear Fecha y Hora
        lblFecha.setText("Fecha: " + sale.getSaleDate().format(DATE_FORMATTER));
        lblHora.setText("Hora: " + sale.getSaleDate().format(TIME_FORMATTER));
        
        // 3. Llenar los totales
        lblTotal.setText("Total de venta: " + CURRENCY_FORMAT.format(sale.getTotalAmount()));
        lblMetodo.setText("Método de pago: " + sale.getPaymentMethod());
        lblRecibido.setText("Recibido: " + CURRENCY_FORMAT.format(sale.getReceivedAmount()));
        lblCambio.setText("Cambio: " + CURRENCY_FORMAT.format(sale.getChange()));

        // 4. Llenar la tabla de productos
        // Asegúrate de que el objeto 'sale' que llega aquí ya fue procesado por SaleService.loadProductDetails
        if (sale.getItems() != null) {
            itemsData.setAll(sale.getItems());
        } else {
            itemsData.clear();
        }
    }

    @FXML
    void onCerrar(ActionEvent event) {
        // Cierra la ventana flotante actual.
        Navigator.closeStage(event);
    }

    @FXML
    void onImprimir(ActionEvent event) {
        // Lógica de impresión
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impresión");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad de impresión en desarrollo.");
        alert.showAndWait();
    }
}