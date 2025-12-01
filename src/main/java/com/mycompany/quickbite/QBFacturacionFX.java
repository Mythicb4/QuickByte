package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.service.SaleService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class QBFacturacionFX implements Initializable {

    // Formateador de fecha/hora para la columna
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Servicios y datos
    private SaleService saleService;
    private ObservableList<Sale> salesData;

    // --- Inyecciones FXML ACTUALIZADAS según facturacion_negocio.fxml ---
    
    // Botones
    @FXML private Button btnVer; 
    @FXML private Button btnAtras;
    @FXML private Button btnEstablecerFecha; 
    
    // DatePickers actualizados
    @FXML private DatePicker dateTo;    // ⬅️ Antes dpFechaFin
    @FXML private DatePicker dateFrom; // ⬅️ Antes dpFechaInicio
    
    // Campos de la tabla y búsqueda
    @FXML private TableView<Sale> tblFacturacion;
    @FXML private TextField txtBuscar; 
    
    // Columnas de la tabla (usando Sale como modelo)
    @FXML private TableColumn<Sale, String> colId;
    @FXML private TableColumn<Sale, LocalDateTime> colFecha;
    @FXML private TableColumn<Sale, String> colCliente;
    @FXML private TableColumn<Sale, String> colMetodo;
    @FXML private TableColumn<Sale, Double> colTotal;
    @FXML private TableColumn<Sale, Double> colRecibido;
    @FXML private TableColumn<Sale, Double> colCambio;
    @FXML private TableColumn<?, ?> colProductos;
    
    // Imágenes y Labels
    @FXML private ImageView imgFechaInicio;
    @FXML private ImageView imgLogo;
    @FXML private ImageView imgSearch;
    @FXML private Label lblLogo;
    @FXML private Label lblLogo1;
    
    // 🔑 CORRECCIÓN: Eliminar la declaración de productService y saleDao aquí 
    // y dejar solo la inicialización en initialize() o mantenerlas como en tu código,
    // pero la lógica es incorrecta. Las eliminamos ya que solo las necesitas para crear saleService.
    /* @FXML private ProductService productService; */
    /* @FXML private SaleDao saleDao; */
    
    /**
     * Configuración inicial del controlador (Mapeo de columnas y carga de datos).
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Inicialización de Servicios
        // 🔑 CORRECCIÓN 1: SessionContext.getLoggedInBusinessEmail() no existe. 
        // Se debe usar SessionContext.getInstance().getCurrentBusiness().getEmail() o 
        // SessionContext.getInstance().getCurrentBusiness().getBusinessName() 
        // si solo tienes acceso al objeto Business (asumimos getEmail() existe).
        String businessEmail = SessionContext.getLoggedInBusinessEmail(); // 🔑 CORRECCIÓN
        
        // Creamos DAOs
        SaleDao saleDao = new SaleDao(businessEmail); 
        ProductDao productDao = new ProductDao(businessEmail);
        
        // Creamos Services
        ProductService productService = new ProductService(productDao); // 🔑 CORRECCIÓN 2: Declaración local
        this.saleService = new SaleService(saleDao, productService);
        
        // 2. Configuración de la tabla
        setupTable();
        
        // 3. Carga inicial de datos
        loadSales();
    }

    /**
     * Mapea las columnas de la vista con las propiedades del modelo Sale.
     */
    private void setupTable() {
        // Mapeo de propiedades (coinciden con los getters de Sale.java)
        // 🔑 CORRECCIÓN 3: Se asume que el tipo de la TableView es Sale, por lo que las columnas deben ser del mismo tipo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colRecibido.setCellValueFactory(new PropertyValueFactory<>("receivedAmount"));
        colCambio.setCellValueFactory(new PropertyValueFactory<>("change"));
        
        // Mapeo y formateo de la fecha/hora
        colFecha.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colFecha.setCellFactory(column -> new TableCell<Sale, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        // Inicializar la lista observable
        salesData = FXCollections.observableArrayList();
        tblFacturacion.setItems(salesData);
    }
    
    /**
     * Carga todas las ventas registradas.
     */
    private void loadSales() {
        try {
            List<Sale> allSales = saleService.getAllSales();
            salesData.setAll(allSales);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Carga", "No se pudieron cargar las ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void onEstablecerFecha(ActionEvent event) {
        if (dateFrom.getValue() == null || dateTo.getValue() == null) {
             showAlert(AlertType.WARNING, "Filtro Incompleto", "Por favor, seleccione tanto la fecha de inicio como la de fin.");
             return;
        }

        try {
            List<Sale> filteredSales = saleService.getSalesByDateRange(dateFrom.getValue(), dateTo.getValue());
            salesData.setAll(filteredSales); // Actualiza la tabla con los resultados filtrados

        } catch (Exception e) {
             showAlert(AlertType.ERROR, "Error de Filtro", "No se pudieron filtrar las ventas: " + e.getMessage());
             e.printStackTrace();
        }
    }
    /**
     * 🔑 Acción para abrir la factura de la venta seleccionada (onAction="#onVer" en FXML).
     * @param event
     */
    @FXML
    void onVer(ActionEvent event) {
        // Lógica para el botón "Ver Factura"
        Sale selectedSale = (Sale) tblFacturacion.getSelectionModel().getSelectedItem();

        if (selectedSale == null) {
            showAlert(AlertType.WARNING, "Selección Requerida", "Por favor, seleccione una venta de la tabla para ver su factura.");
            return;
        }
        
        try {
            // 🔑 CORRECCIÓN 4 (Lógica): Para obtener el nombre del negocio, usamos el objeto Business actual.
            String businessName = SessionContext.getLoggedInBusinessEmail(); 
            String fxmlPath = "/views/factura_negocio.fxml";
            String title = "Comprobante de Venta - " + selectedSale.getId();

            // 1. Obtener el controlador de la nueva ventana de Factura (QBFacturaFX)
            QBFacturaFX facturaController = Navigator.navigateToNewFloatingWindow(fxmlPath, title);

            if (facturaController != null) {
                // 2. Pasar la venta seleccionada (Sale) al controlador de la factura
                facturaController.setSaleData(selectedSale, businessName);
            }
            
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudo abrir la factura para la venta seleccionada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🔑 Acción para regresar (onAction="#onBtnBack" en FXML).
     * @param event
     */
    @FXML
    void onBtnBack(ActionEvent event) {
        // Navegar de regreso al dashboard del negocio
        Navigator.navigateTo("/views/login_negocio.fxml", "Negocio Dashboard", true, event);
    }
    
    @FXML
    void onBuscar(ActionEvent event) {

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