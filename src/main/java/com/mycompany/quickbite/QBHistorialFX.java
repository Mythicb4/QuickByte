package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.StudentPurchaseDao;
import com.mycompany.quickbite.model.PurchaseItem;
import com.mycompany.quickbite.model.StudentPurchase;
import com.mycompany.quickbite.util.AppState;
import com.mycompany.quickbite.util.CarritoManager;
import com.mycompany.quickbite.util.Navigator;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QBHistorialFX implements Initializable {

    private StudentPurchaseDao purchaseDao;
    private ObservableList<StudentPurchase> purchaseList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Button btnAtras;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnEstablecerFecha;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnTienda;

    @FXML
    private TableColumn<StudentPurchase, String> colCafeteria;

    @FXML
    private TableColumn<StudentPurchase, Double> colCosto;

    @FXML
    private TableColumn<StudentPurchase, String> colFecha;

    @FXML
    private TableColumn<StudentPurchase, String> colMetodo;

    @FXML
    private TableColumn<StudentPurchase, String> colTProductos;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private TableView<StudentPurchase> tblCompras;

    @FXML
    private Text txtTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String studentEmail = AppState.getUserEmail();
            if (studentEmail == null || studentEmail.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "No se pudo cargar el historial: usuario no identificado.")
                        .showAndWait();
                return;
            }

            // Inicializar DAO
            purchaseDao = new StudentPurchaseDao(studentEmail);

            // Configurar columnas de la tabla
            setupTableColumns();

            // Cargar todas las compras inicialmente
            loadAllPurchases();

        } catch (Exception e) {
            System.err.println("Error inicializando historial: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar el historial: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Configura las columnas de la tabla
     */
    private void setupTableColumns() {
        // Columna de Fecha
        colFecha.setCellValueFactory(cellData -> {
            StudentPurchase purchase = (StudentPurchase) cellData.getValue();
            String formattedDate = purchase.getPurchaseDate().format(DISPLAY_FORMATTER);
            return new SimpleStringProperty(formattedDate);
        });

        // Columna de Cafetería
        colCafeteria.setCellValueFactory(cellData -> {
            StudentPurchase purchase = (StudentPurchase) cellData.getValue();
            String cafeteria = purchase.getBusinessName();
            if (purchase.getBusinessLocation() != null && !purchase.getBusinessLocation().isEmpty()) {
                cafeteria += " (" + purchase.getBusinessLocation() + ")";
            }
            return new SimpleStringProperty(cafeteria);
        });

        // Columna de Costo
        colCosto.setCellValueFactory(cellData -> {
            StudentPurchase purchase = (StudentPurchase) cellData.getValue();
            return new SimpleDoubleProperty(purchase.getTotalAmount()).asObject();
        });

        // Formatear la columna de costo como moneda
        colCosto.setCellFactory(column -> new javafx.scene.control.TableCell<StudentPurchase, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$ %.2f", item));
                }
            }
        });

        // Columna de Total de Productos (lista de productos)
        colTProductos.setCellValueFactory(cellData -> {
            StudentPurchase purchase = (StudentPurchase) cellData.getValue();
            String productos = purchase.getItems().stream()
                    .map(item -> item.getQuantity() + "x " + item.getProductName())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(productos);
        });

        // Columna de Método de Pago
        colMetodo.setCellValueFactory(cellData -> {
            StudentPurchase purchase = (StudentPurchase) cellData.getValue();
            return new SimpleStringProperty(purchase.getPaymentMethod());
        });
    }

    /**
     * Carga todas las compras del estudiante
     */
    private void loadAllPurchases() {
        List<StudentPurchase> allPurchases = purchaseDao.loadAll();
        purchaseList.setAll(allPurchases);
        tblCompras.setItems(purchaseList);
        updateTotalDisplay(allPurchases);
    }

    /**
     * Actualiza el total gastado mostrado
     */
    private void updateTotalDisplay(List<StudentPurchase> purchases) {
        double total = purchases.stream()
                .mapToDouble(StudentPurchase::getTotalAmount)
                .sum();
        txtTotal.setText(String.format("$ %.2f", total));
    }

    @FXML
    void onCalendar(MouseEvent event) {
        // Placeholder para abrir selector de fechas (opcional)
    }

    @FXML
    void onEstablecerFechaAction(ActionEvent event) {
        try {
            // Obtener las fechas de los DatePickers
            LocalDate fechaInicio = dpFechaInicio.getValue();
            LocalDate fechaFin = dpFechaFin.getValue();

            // Validar que ambas fechas estén presentes
            if (fechaInicio == null || fechaFin == null) {
                new Alert(Alert.AlertType.WARNING,
                        "Por favor seleccione ambas fechas usando los selectores de fecha.").showAndWait();
                return;
            }

            // Validar que la fecha de inicio no sea posterior a la fecha final
            if (fechaInicio.isAfter(fechaFin)) {
                new Alert(Alert.AlertType.WARNING, "La fecha de inicio no puede ser posterior a la fecha final.")
                        .showAndWait();
                return;
            }

            // Filtrar compras por rango de fechas
            List<StudentPurchase> filteredPurchases = purchaseDao.getPurchasesByDateRange(fechaInicio, fechaFin);
            purchaseList.setAll(filteredPurchases);
            tblCompras.setItems(purchaseList);
            updateTotalDisplay(filteredPurchases);

            // Mensaje informativo
            new Alert(Alert.AlertType.INFORMATION,
                    String.format("Se encontraron %d compras entre %s y %s",
                            filteredPurchases.size(),
                            fechaInicio.format(DATE_FORMATTER),
                            fechaFin.format(DATE_FORMATTER)))
                    .showAndWait();

        } catch (Exception e) {
            System.err.println("Error filtrando por fechas: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al filtrar por fechas: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onAtras(ActionEvent event) {
        Navigator.navigateTo("/views/login_estudiante.fxml", "estudiante", true, event);
    }

    @FXML
    private void handleViewCartAndGenerateQR(ActionEvent event) {

        // Verificar si hay productos en el carrito antes de navegar.
        if (CarritoManager.getInstancia().getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El carrito está vacío. Agregue productos antes de generar el QR.")
                    .showAndWait();
            return;
        }

        Navigator.navigateTo("/views/carrito.fxml", "QuickBite - Mi Carrito", false, event);
    }

    @FXML
    void onBtnSalir(ActionEvent event) {
        // Nota: El método handleLogout anterior vaciaba el carrito.
        // Si el botón Salir debe vaciar el carrito, puedes añadir:
        // CarritoManager.getInstancia().vaciarCarrito();
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
    }

    @FXML
    void onHistorial(ActionEvent event) {
        Navigator.navigateTo("/views/historial_estudiante.fxml", "historial", true, event);
    }

    @FXML
    void onPerfil(ActionEvent event) {
        Navigator.navigateTo("/views/perfil_estudiante.fxml", "perfil", false, event);
    }

    @FXML
    void onPreferencias(ActionEvent event) {
        Navigator.navigateTo("/views/preferencia_estudiante.fxml", "preferencia", true, event);
    }

    @FXML
    void onTienda(ActionEvent event) {
        Navigator.navigateTo("/views/tiendas_estudiante.fxml", "tiendas", true, event);
    }

}
