package com.mycompany.quickbite;

import com.mycompany.quickbite.dao.SaleDao;
import com.mycompany.quickbite.model.Sale;
import com.mycompany.quickbite.util.Navigator;
import com.mycompany.quickbite.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class QBNegocioFX implements Initializable {

    @FXML
    private HBox HBCompraExpress;

    @FXML
    private HBox HBCompraExpress1;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnHistorial1;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnPerfil1;

    @FXML
    private Button btnPreferencias;

    @FXML
    private Button btnSalir;

    @FXML
    private Button btnTienda;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblLogo1;

    @FXML
    private LineChart<Number, Number> chartVentas;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private SaleDao saleDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String businessEmail = SessionContext.getLoggedInBusinessEmail();
        if (businessEmail != null) {
            saleDao = new SaleDao(businessEmail);
            loadDailySalesChart();
        }
    }

    private void loadDailySalesChart() {
        try {
            // Obtener todas las ventas del día actual
            List<Sale> todaySales = saleDao.loadAll().stream()
                    .filter(sale -> sale.getSaleDate() != null &&
                            sale.getSaleDate().toLocalDate().equals(LocalDate.now()))
                    .sorted(Comparator.comparing(Sale::getSaleDate))
                    .collect(Collectors.toList());

            // Limpiar el gráfico
            chartVentas.getData().clear();

            if (todaySales.isEmpty()) {
                // Si no hay ventas, mostrar gráfico vacío
                yAxis.setLabel("Cantidad de Ventas");
                xAxis.setLabel("Hora del Día");
                return;
            }

            // Obtener hora de primera y última venta
            LocalDateTime firstSale = todaySales.get(0).getSaleDate();
            LocalDateTime lastSale = todaySales.get(todaySales.size() - 1).getSaleDate();

            // Crear intervalos de 3 horas
            int startHour = (firstSale.getHour() / 3) * 3; // Redondear al intervalo de 3h inferior
            int endHour = ((lastSale.getHour() / 3) + 1) * 3; // Redondear al intervalo de 3h superior
            if (endHour > 24)
                endHour = 24;

            // Agrupar ventas por intervalos de 3 horas
            Map<Integer, Integer> salesByInterval = new TreeMap<>();
            for (int hour = startHour; hour < endHour; hour += 3) {
                salesByInterval.put(hour, 0);
            }

            // Contar ventas en cada intervalo
            for (Sale sale : todaySales) {
                int hour = sale.getSaleDate().getHour();
                int interval = (hour / 3) * 3;
                salesByInterval.put(interval, salesByInterval.getOrDefault(interval, 0) + 1);
            }

            // Crear serie de datos
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Ventas de Hoy");

            // Si solo hay una venta, agregar punto en 0 para mostrar la línea
            if (todaySales.size() == 1) {
                series.getData().add(new XYChart.Data<>(startHour, 0));
            }

            // Agregar datos a la serie
            for (Map.Entry<Integer, Integer> entry : salesByInterval.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            // Agregar la serie al gráfico
            chartVentas.getData().add(series);

            // Configurar ejes
            yAxis.setLabel("Cantidad de Ventas");
            xAxis.setLabel("Hora del Día (Intervalos de 3h)");
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(startHour);
            xAxis.setUpperBound(endHour);
            xAxis.setTickUnit(3);

            yAxis.setAutoRanging(true);

        } catch (Exception e) {
            System.err.println("Error cargando gráfica de ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onBtnSalir(ActionEvent event) {
        Navigator.navigateTo("/views/login.fxml", "login", true, event);
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
        Navigator.navigateTo("/views/qr_scanner_negocio.fxml", "Escanear QR", true, event);
    }

    @FXML
    void onReportes(ActionEvent event) {
        Navigator.navigateTo("/views/reportes_negocio.fxml", "reportes", true, event);
    }

    @FXML
    void onVentas(ActionEvent event) {
        Navigator.navigateTo("/views/ventas_negocio.fxml", "ventas", false, event);
    }
}
