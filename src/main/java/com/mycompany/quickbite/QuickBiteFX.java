package com.mycompany.quickbite;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class QuickBiteFX extends Application {

    private final Inventory inventory = new Inventory();
    private final DeepLinkRouter router;
    private final ObservableList<ProductVM> items;

    public QuickBiteFX() {
        this.router = new DeepLinkRouter(this.inventory);
        this.items = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage stage) {
        // Datos iniciales
        inventory.addOrUpdate("Manzana", 2.5, 10);
        inventory.addOrUpdate("Pan", 1.2, 5);
        refreshItems();

        // Tabla
        TableView<ProductVM> table = new TableView<>(items);
        TableColumn<ProductVM, String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<ProductVM, Double> colPrice = new TableColumn<>("Precio");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<ProductVM, Integer> colQty = new TableColumn<>("Cantidad");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table.getColumns().addAll(colName, colPrice, colQty);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Formulario agregar/eliminar
        TextField tfName = new TextField();
        tfName.setPromptText("Nombre");
        TextField tfPrice = new TextField();
        tfPrice.setPromptText("Precio");
        TextField tfQty = new TextField();
        tfQty.setPromptText("Cantidad");

        Button btnSave = new Button("Guardar");
        Button btnDelete = new Button("Eliminar");

        HBox formBox = new HBox(8, tfName, tfPrice, tfQty, btnSave, btnDelete);
        formBox.setAlignment(Pos.CENTER_LEFT);

        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim();
            double price = parseDoubleLocal(tfPrice.getText().trim(), 0.0);
            int qty = parseIntLocal(tfQty.getText().trim(), 0);

            if (name.isEmpty()) {
                showInfo("Nombre requerido");
            } else {
                inventory.addOrUpdate(name, price, qty);
                refreshItems();
            }
        });

        btnDelete.setOnAction(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                showInfo("Nombre requerido");
            } else {
                boolean removed = inventory.remove(name);
                if (!removed) showInfo("No encontrado");
                refreshItems();
            }
        });

        // Deeplink
        TextField tfDeeplink = new TextField();
        tfDeeplink.setPromptText("quickbite://add?name=...&price=...&qty=...");
        Button btnDeeplink = new Button("Abrir deeplink");
        HBox deeplinkBox = new HBox(8, tfDeeplink, btnDeeplink);
        deeplinkBox.setAlignment(Pos.CENTER_LEFT);

        btnDeeplink.setOnAction(e -> {
            String result = router.handle(tfDeeplink.getText().trim());
            showInfo(result);
            refreshItems();
        });

        router.setOnInventoryViewRequested(() -> Platform.runLater(stage::toFront));

        // Layout
        Pane rootPane = new Pane();
        Pane titlePane = new Pane();
        titlePane.setPrefSize(190, 500);
        titlePane.getStyleClass().add("title-container");

        HBox headerBox = header("QuickBite FX");
        titlePane.getChildren().add(headerBox);
        headerBox.setLayoutX(60 - headerBox.getBoundsInLocal().getWidth() / 2);
        headerBox.setLayoutY(20 - headerBox.getBoundsInLocal().getHeight() / 2);

        VBox formVBox = new VBox(12, formBox);
        VBox deeplinkVBox = new VBox(12, new Separator(), new Text("Deeplink"), deeplinkBox);

        titlePane.setLayoutX(0);
        titlePane.setLayoutY(0);
        formVBox.setLayoutX(200);
        formVBox.setLayoutY(20);
        table.setLayoutX(200);
        table.setLayoutY(60);
        table.setPrefWidth(600);
        table.setPrefHeight(320);
        deeplinkVBox.setLayoutX(200);
        deeplinkVBox.setLayoutY(400);
        deeplinkVBox.setPrefWidth(900);

        rootPane.getChildren().addAll(titlePane, formVBox, table, deeplinkVBox);
        rootPane.setPadding(new Insets(16));
        rootPane.getStyleClass().add("root-pane");

        Scene scene = new Scene(rootPane, 900, 500);
        java.net.URL cssUrl = QBloginFX.class.getResource("/quickbite.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: QBlogin.css no encontrado en src/main/resources");
        }

        stage.setTitle("QuickBite FX");
        stage.setScene(scene);
        Navigator.init(stage);
        stage.show();

        // Procesar argumentos tipo deeplink al iniciar
        List<String> args = getParameters().getUnnamed();
        if (args != null) {
            for (String arg : args) {
                String result = router.handle(arg);
                System.out.println("Deeplink: " + arg + " => " + result);
            }
            refreshItems();
        }
    }

    private void refreshItems() {
        items.setAll(inventory.all());
    }

    private static double parseDoubleLocal(String text, double def) {
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return def;
        }
    }

    private static int parseIntLocal(String text, int def) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return def;
        }
    }

    private static HBox header(String title) {
        Text txt = new Text(title);
        txt.getStyleClass().add("title");
        HBox box = new HBox(txt);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static void showInfo(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("QuickBite FX");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
