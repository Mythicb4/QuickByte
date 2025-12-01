package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.ProductDao;
import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Product;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.service.ProductService;
import com.mycompany.quickbite.service.ReportGenerator;
import com.mycompany.quickbite.service.ReportService; // ⬅️ Nuevo Servicio
import com.mycompany.quickbite.service.SaleService;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker; // ⬅️ Se asume para la gestión de fechas
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Asumiendo que BusinessDao y ProductDao están inyectados en sus respectivos Services
public class QBReportesFX implements Initializable {

    // --- Servicios y Formato ---
    private ReportService reportService;
    private ReportGenerator reportGenerator;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    // --- FXML IDs (del archivo reportes_negocio.fxml) ---
    // Dashboard Stats
    @FXML private Text txtIngresos;
    @FXML private Text txtSalidas;
    @FXML private Text txtGanancias;
    
    // Filtro de Fechas
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Button btnEstablecerFecha;
    
    // Tablas
    @FXML private TableView<ProductSalesStats> tblMasVendidos;
    @FXML private TableColumn<ProductSalesStats, String> colName;
    @FXML private TableColumn<ProductSalesStats, Double> colCosto; // Asumo que el nombre en FXML es solo "Costo"
    @FXML private TableColumn<ProductSalesStats, Integer> colStock; // No tiene sentido en esta tabla, pero lo mapeamos
    @FXML private TableColumn<ProductSalesStats, String> colTotalVentas; // El ID colProvider se usó para "Ventas Totales"
    
    @FXML private TableView<ProductSalesStats> tblMenosVendidos;
    @FXML private TableColumn<ProductSalesStats, String> colName1;
    @FXML private TableColumn<ProductSalesStats, Double> colCosto1; // Asumo que el nombre en FXML es solo "Costo"
    @FXML private TableColumn<ProductSalesStats, Integer> colStock1;
    @FXML private TableColumn<ProductSalesStats, String> colTotalVentas1; // El ID colProvider1 se usó para "Ventas Totales"

    // Componentes del Menú (Para la navegación)
    @FXML private Button btnAtras;
    @FXML private Button btnClientes;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnFacturacion;
    @FXML private Button btnInventario;
    @FXML private Button btnProduct;
    @FXML private Button btnQr;
    @FXML private Button btnReportes;
    @FXML private Button btnVentas;
    @FXML private ImageView imgLogo;
    @FXML private Label lblLogo;
    @FXML private Label lblLogo1;
    
    // --- Lógica de Inicialización ---

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar el servicio con el email del negocio de la sesión
        String businessEmail = SessionContext.getLoggedInBusinessEmail();
        ProductDao productDao = new ProductDao(businessEmail);
        SaleDao saleDao = new SaleDao(businessEmail);

        // 3. Inicializar los Servicios
        SaleService saleService = new SaleService(saleDao, new ProductService(productDao)); // ⬅️ LÍNEA 85 CORREGIDA
        ProductService productService = new ProductService(productDao);

        // Inicializar el servicio de reportes, que depende de los anteriores
        this.reportService = new ReportService(saleService, productService);

        // 1. Configurar las columnas de las tablas
        configureTable(tblMasVendidos, colName, colCosto, colStock, colTotalVentas);
        configureTable(tblMenosVendidos, colName1, colCosto1, colStock1, colTotalVentas1);

        // 2. Establecer el rango de fechas inicial (ej. Últimos 30 días)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        dateFrom.setValue(startDate);
        dateTo.setValue(endDate);

        // 3. Cargar los datos iniciales
        loadReportData(startDate, endDate);
    }
    
    /**
     * Método de utilidad para configurar las columnas de una tabla.
     */
    private void configureTable(TableView<ProductSalesStats> tableView, 
                                TableColumn<ProductSalesStats, String> nameCol, 
                                TableColumn<ProductSalesStats, Double> costCol,
                                TableColumn<ProductSalesStats, Integer> stockCol,
                                TableColumn<ProductSalesStats, String> salesCol) {
        
        // Mapeamos las propiedades del objeto ProductSalesStats a las columnas
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        salesCol.setCellValueFactory(new PropertyValueFactory<>("non"));
    }

    // --- Lógica de Reportes y Carga de Datos ---

    @FXML
    void onEstablecerFechaAction(ActionEvent event) {
        LocalDate start = dateFrom.getValue();
        LocalDate end = dateTo.getValue();
        
        if (start != null && end != null && !start.isAfter(end)) {
            loadReportData(start, end);
        } else {
            // Manejo de error si las fechas son inválidas
            // (Ej: dateFrom es posterior a dateTo)
        }
    }
    
    private void loadReportData(LocalDate start, LocalDate end) {
        // Obtenemos los datos desde el servicio
        ReportService.SalesSummary summary = reportService.generateSalesSummary(start, end);

        // 1. Actualizar Resumen de Cifras
        txtIngresos.setText(currencyFormat.format(summary.totalRevenue()));
        txtSalidas.setText(currencyFormat.format(summary.totalCost()));
        txtGanancias.setText(currencyFormat.format(summary.totalProfit()));
        
        // 2. Actualizar Tablas de Productos
        
        // Mas Vendidos (Orden descendente por cantidad vendida)
        // LÍNEA 156 - Más Vendidos
        List<ProductSalesStats> mostSold = summary.productSalesStats().stream()
            .sorted(Comparator.comparingInt((ProductSalesStats stats) -> stats.getTotalQuantitySold()).reversed()) // ⬅️ CAMBIO AQUÍ
            .limit(10)
            .collect(Collectors.toList());
        tblMasVendidos.setItems(FXCollections.observableList(mostSold));

        // LÍNEA 163 - Menos Vendidos
        List<ProductSalesStats> leastSold = summary.productSalesStats().stream()
            .sorted(Comparator.comparingInt((ProductSalesStats stats) -> stats.getTotalQuantitySold())) // ⬅️ CAMBIO AQUÍ
            .limit(10)
            .collect(Collectors.toList());
        tblMenosVendidos.setItems(FXCollections.observableList(leastSold));
        
        // Nota: Las columnas Stock y Costo en ambas tablas ahora reflejan los valores actuales del Producto.
    }
    
    // --- Clases de Modelo/DTO anidadas (Necesarias para las Tablas) ---
    
    /**
     * DTO para mostrar las estadísticas de ventas por producto en las tablas.
     */
    public record ProductSalesStats(
            String productId,
            String productName,
            double cost, // Costo unitario actual del producto
            int currentStock, // Stock actual del producto
            int totalQuantitySold
    ) {
        // Métodos auxiliares para FXCollections (aunque no estrictamente necesarios para Records)
        public String getProductName() { return productName; }
        public double getCost() { return cost; }
        public int getCurrentStock() { return currentStock; }
        public int getTotalQuantitySold() { return totalQuantitySold; }
    }

    // --- Navegación (Métodos FXML) ---

    @FXML
    void onBtnBack(ActionEvent event) {
        Navigator.navigateTo("/views/login_negocio.fxml", "dashboard_negocio", true, event);
    }

    @FXML
    void onClientes(ActionEvent event) {
        Navigator.navigateTo("/views/cliente_negocio.fxml", "cliente", true, event);
    }

    @FXML
    void onConfiguracion(ActionEvent event) {
        Navigator.navigateTo("/views/configuracion_negocio.fxml", "configuracion", false, event);
    }

    @FXML
    void onFacturacion(ActionEvent event) {
        Navigator.navigateTo("/views/facturacion_negocio.fxml", "facturacion", true, event);
    }

    @FXML
    void onInventario(ActionEvent event) {
        Navigator.navigateTo("/views/inventario_negocio.fxml", "inventario", true, event);
    }

    @FXML
    void onProduct(ActionEvent event) {
        Navigator.navigateTo("/views/producto_negocio.fxml", "producto", true, event);
    }

    @FXML
    void onQr(ActionEvent event) {
        // Agregar el link a qr para leer
    }

    @FXML
    void onVentas(ActionEvent event) {
        // Navegar a ventas si es necesario, o abrir la ventana flotante de registro
        Navigator.navigateTo("/views/ventas_negocio.fxml", "ventas", false, event);
    }
    
    @FXML
    void onReportes(ActionEvent event) {
        Navigator.navigateTo("/views/reportes_negocio.fxml", "reportes", true, event);
    }
}
