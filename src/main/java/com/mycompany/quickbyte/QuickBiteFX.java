package com.mycompany.quickbyte;

// ===== IMPORTS NECESARIOS =====
import java.net.URI;                    // Para manejar URIs de deeplinks
import java.util.*;                     // Colecciones (Map, HashMap, etc.)
import javafx.application.Application;  // Clase base para aplicaciones JavaFX
import javafx.application.Platform;     // Para ejecutar código en el hilo de UI
import javafx.beans.property.DoubleProperty;     // Propiedad observable para números decimales
import javafx.beans.property.IntegerProperty;    // Propiedad observable para enteros
import javafx.beans.property.SimpleDoubleProperty;   // Implementación de DoubleProperty
import javafx.beans.property.SimpleIntegerProperty;  // Implementación de IntegerProperty
import javafx.beans.property.SimpleStringProperty;   // Implementación de StringProperty
import javafx.beans.property.StringProperty;         // Propiedad observable para texto
import javafx.collections.FXCollections;  // Utilidades para colecciones observables
import javafx.collections.ObservableList; // Lista que notifica cambios automáticamente
import javafx.geometry.Insets;           // Para márgenes y espaciado
import javafx.geometry.Pos;              // Para alineación de elementos
import javafx.scene.Scene;               // Escena principal de JavaFX
import javafx.scene.control.*;           // Controles de UI (botones, campos de texto, etc.)
import javafx.scene.control.cell.PropertyValueFactory; // Para conectar datos con celdas de tabla
import javafx.scene.layout.*;            // Contenedores de layout (VBox, HBox, etc.)
import javafx.scene.text.Text;           // Texto simple
import javafx.stage.Stage;               // Ventana principal

/**
 * QuickBiteFX - Aplicación de inventario con interfaz moderna
 * 
 * Esta aplicación demuestra:
 * 1. Interfaz de usuario moderna con JavaFX y CSS
 * 2. Sistema de inventario en memoria con operaciones CRUD
 * 3. Sistema de deeplinks personalizado (quickbite://...)
 * 
 * La clase extiende Application, que es el punto de entrada para aplicaciones JavaFX
 */
public class QuickBiteFX extends Application {

    // ===== CLASE PRODUCTO OBSERVABLE =====
    /**
     * ProductVM (Product View Model) - Representa un producto en la interfaz
     * 
     * Usa propiedades observables de JavaFX que notifican automáticamente
     * a la UI cuando cambian los valores. Esto permite que la tabla se
     * actualice automáticamente sin código adicional.
     */
    public static class ProductVM {
        // Propiedades observables que notifican cambios a la UI automáticamente
        private final StringProperty name = new SimpleStringProperty();        // Nombre del producto
        private final DoubleProperty price = new SimpleDoubleProperty();       // Precio del producto
        private final IntegerProperty quantity = new SimpleIntegerProperty();  // Cantidad en stock

        /**
         * Constructor - Inicializa un producto con sus valores
         * @param name Nombre del producto
         * @param price Precio del producto
         * @param quantity Cantidad disponible
         */
        public ProductVM(String name, double price, int quantity) {
            this.name.set(name);        // Establece el nombre usando la propiedad observable
            this.price.set(price);      // Establece el precio usando la propiedad observable
            this.quantity.set(quantity); // Establece la cantidad usando la propiedad observable
        }

        // ===== GETTERS Y SETTERS =====
        // Estos métodos permiten acceder y modificar los valores de las propiedades
        
        public String getName() { return name.get(); }                    // Obtiene el nombre actual
        public void setName(String value) { name.set(value); }            // Cambia el nombre
        public StringProperty nameProperty() { return name; }             // Devuelve la propiedad para binding

        public double getPrice() { return price.get(); }                  // Obtiene el precio actual
        public void setPrice(double value) { price.set(value); }          // Cambia el precio
        public DoubleProperty priceProperty() { return price; }           // Devuelve la propiedad para binding

        public int getQuantity() { return quantity.get(); }               // Obtiene la cantidad actual
        public void setQuantity(int value) { quantity.set(value); }       // Cambia la cantidad
        public IntegerProperty quantityProperty() { return quantity; }    // Devuelve la propiedad para binding
    }

    // ===== CLASE INVENTARIO =====
    /**
     * Inventory - Maneja la colección de productos en memoria
     * 
     * Usa un HashMap para almacenar productos por nombre, permitiendo
     * búsquedas rápidas y operaciones CRUD eficientes.
     */
    public static class Inventory {
        // Mapa que almacena productos usando el nombre como clave
        // Esto permite búsquedas rápidas por nombre (O(1) en promedio)
        private final Map<String, ProductVM> byName = new HashMap<>();

        /**
         * Agrega un nuevo producto o actualiza uno existente
         * @param name Nombre del producto (clave única)
         * @param price Precio del producto
         * @param qty Cantidad en stock
         */
        public void addOrUpdate(String name, double price, int qty) {
            ProductVM p = byName.get(name);  // Busca si ya existe un producto con este nombre
            if (p == null) {
                // Si no existe, crea un nuevo producto y lo agrega al mapa
                byName.put(name, new ProductVM(name, price, qty));
            } else {
                // Si ya existe, actualiza solo el precio y cantidad (mantiene la misma instancia)
                p.setPrice(price);
                p.setQuantity(qty);
            }
        }

        /**
         * Elimina un producto del inventario
         * @param name Nombre del producto a eliminar
         * @return true si se eliminó, false si no existía
         */
        public boolean remove(String name) { 
            return byName.remove(name) != null;  // remove() devuelve null si no existe
        }
        
        /**
         * Obtiene todos los productos del inventario
         * @return Colección con todos los productos
         */
        public Collection<ProductVM> all() { 
            return byName.values();  // Devuelve solo los valores (productos) del mapa
        }
        
        /**
         * Busca un producto por nombre
         * @param name Nombre del producto a buscar
         * @return El producto si existe, null si no
         */
        public ProductVM get(String name) { 
            return byName.get(name);  // Búsqueda rápida por clave
        }
    }

    // ===== CLASE MANEJADOR DE DEEPLINKS =====
    /**
     * DeepLinkRouter - Procesa URIs personalizadas del tipo quickbite://
     * 
     * Los deeplinks permiten controlar la aplicación desde fuera,
     * como desde la línea de comandos o desde otras aplicaciones.
     * 
     * Formato: quickbite://accion?parametro1=valor1&parametro2=valor2
     * Ejemplos:
     * - quickbite://add?name=Manzana&price=2.5&qty=10
     * - quickbite://remove?name=Manzana
     * - quickbite://open?view=inventory
     */
    public static class DeepLinkRouter {
        private final Inventory inventory;  // Referencia al inventario para operacionesfinal 
            //- No se puede cambiar después de la inicialización 
            //- Inventory inventory - Variable que almacena una referencia al inventario
        
        private Runnable onInventoryViewRequested = () -> {};  // Callback para mostrar inventario
            //- onInventoryViewRequested - Variable que almacena una referencia a la función que se ejecutará cuando se solicite mostrar el inventario
            //- Runnable - Tipo de dato que representa una función sin parámetros y que devuelve void
            //- () -> {} - Función vacía que se ejecuta cuando se solicite mostrar el inventario
        /**
         * Constructor - Inicializa el router con una referencia al inventario
         * @param inventory El inventario que manejará este router
         */
        public DeepLinkRouter(Inventory inventory) { 
            this.inventory = inventory; 
        }
        
        /**
         * Establece el callback que se ejecuta cuando se solicita mostrar el inventario
         * @param cb Función a ejecutar (puede ser null)
         */
        public void setOnInventoryViewRequested(Runnable cb) { 
            this.onInventoryViewRequested = cb != null ? cb : () -> {}; 
        }

        /**
         * Procesa una URI de deeplink y ejecuta la acción correspondiente
         * @param uriString La URI completa (ej: "quickbite://add?name=Manzana&price=2.5")
         * @return Mensaje de resultado de la operación
         */
        public String handle(String uriString) {
            try {
                // Convierte el string en un objeto URI para parsear fácilmente
                URI uri = URI.create(uriString);
                
                // Verifica que el esquema sea "quickbite" (ignorando mayúsculas/minúsculas)
                if (!"quickbite".equalsIgnoreCase(uri.getScheme())) 
                    return "Esquema no soportado";
                
                // Extrae la acción (la parte después de "://" y antes de "?")
                String action = uri.getHost();
                
                // Parsea los parámetros de la query string (después del "?")
                Map<String,String> params = parseQuery(uri.getQuery());
                
                // Verifica que haya una acción especificada
                if (action == null) 
                    return "Acción no especificada";

                // Ejecuta la acción correspondiente usando switch
                switch (action.toLowerCase()) {
                    case "open":
                        // Abre una vista específica
                        String view = params.getOrDefault("view", "inventory");
                        if ("inventory".equalsIgnoreCase(view)) { 
                            onInventoryViewRequested.run();  // Ejecuta el callback
                            return "Vista de inventario"; 
                        }
                        return "Vista no reconocida: " + view;
                        
                    case "add":
                        // Agrega un nuevo producto
                        return addOrUpdate(params);
                        
                    case "update":
                        // Actualiza un producto existente (misma lógica que add)
                        return addOrUpdate(params);
                        
                    case "remove":
                        // Elimina un producto
                        String name = params.get("name");
                        if (name == null || name.isEmpty()) 
                            return "Nombre requerido";
                        boolean ok = inventory.remove(name);
                        return ok ? "Eliminado: " + name : "No encontrado: " + name;
                        
                    default:
                        return "Acción no soportada: " + action;
                }
            } catch (IllegalArgumentException ex) {
                // Si la URI es inválida, devuelve un mensaje de error
                return "URI inválida: " + ex.getMessage();
            }
        }

        /**
         * Método auxiliar para agregar o actualizar un producto desde parámetros de deeplink
         * @param params Mapa con los parámetros parseados de la URI
         * @return Mensaje de resultado
         */
        private String addOrUpdate(Map<String,String> params) {
            String name = params.get("name");  // Obtiene el nombre del parámetro
            if (name == null || name.isEmpty()) 
                return "Nombre requerido";
            
            // Convierte los parámetros de string a números, usando valores por defecto si fallan
            double price = parseDouble(params.get("price"), 0);  // Precio por defecto: 0
            int qty = parseInt(params.get("qty"), 0);            // Cantidad por defecto: 0
            
            // Usa el inventario para agregar/actualizar el producto
            inventory.addOrUpdate(name, price, qty);
            return "Guardado: " + name;
        }

        /**
         * Parsea una query string (ej: "name=Manzana&price=2.5&qty=10") en un mapa
         * @param query La query string a parsear
         * @return Mapa con los parámetros clave-valor
         */
        private static Map<String,String> parseQuery(String query) {
            Map<String,String> map = new HashMap<>();
            if (query == null || query.isEmpty()) 
                return map;  // Devuelve mapa vacío si no hay query
            
            // Divide la query por "&" para obtener cada par clave-valor
            for (String pair : query.split("&")) {
                int i = pair.indexOf('=');  // Busca el separador "="
                if (i > 0) {  // Solo procesa si hay un "=" y está en posición válida
                    // Decodifica la clave y el valor (por si hay caracteres especiales)
                    String k = urlDecode(pair.substring(0, i));      // Clave (antes del "=")
                    String v = urlDecode(pair.substring(i + 1));     // Valor (después del "=")
                    map.put(k, v);  // Agrega al mapa
                }
            }
            return map;
        }

        /**
         * Decodifica una string URL-encoded (convierte %20 a espacios, etc.)
         * @param s String a decodificar
         * @return String decodificada, o la original si falla
         */
        private static String urlDecode(String s) {
            try { 
                return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8.name()); 
            }
            catch (Exception e) { 
                return s;  // Si falla la decodificación, devuelve la string original
            }
        }

        /**
         * Convierte un string a double de forma segura
         * @param s String a convertir
         * @param fb Valor por defecto si la conversión falla
         * @return El número convertido o el valor por defecto
         */
        private static double parseDouble(String s, double fb) {
            if (s == null || s.isEmpty()) 
                return fb;  // Si está vacío, devuelve el valor por defecto
            try { 
                return Double.parseDouble(s); 
            } catch (NumberFormatException e) { 
                return fb;  // Si no es un número válido, devuelve el valor por defecto
            }
        }
        
        /**
         * Convierte un string a int de forma segura
         * @param s String a convertir
         * @param fb Valor por defecto si la conversión falla
         * @return El número convertido o el valor por defecto
         */
        private static int parseInt(String s, int fb) {
            if (s == null || s.isEmpty()) 
                return fb;  // Si está vacío, devuelve el valor por defecto
            try { 
                return Integer.parseInt(s); 
            } catch (NumberFormatException e) { 
                return fb;  // Si no es un número válido, devuelve el valor por defecto
            }
        }
    }

    // ===== VARIABLES DE INSTANCIA =====
    private final Inventory inventory = new Inventory();                    // Inventario de productos
    private final DeepLinkRouter router = new DeepLinkRouter(inventory);   // Manejador de deeplinks
    private final ObservableList<ProductVM> items = FXCollections.observableArrayList(); // Lista observable para la tabla

    /**
     * MÉTODO PRINCIPAL DE LA APLICACIÓN JAVAFX
     * 
     * Este método se ejecuta automáticamente cuando se inicia la aplicación.
     * Aquí se configura toda la interfaz de usuario y se establecen los eventos.
     * 
     * @param stage La ventana principal de la aplicación
     */
    @Override
    public void start(Stage stage) {
        // ===== INICIALIZACIÓN DE DATOS =====
        // Agrega algunos productos de ejemplo al inventario
        inventory.addOrUpdate("Manzana", 2.5, 10);  // Producto: Manzana, precio: $2.50, cantidad: 10
        inventory.addOrUpdate("Pan", 1.2, 5);       // Producto: Pan, precio: $1.20, cantidad: 5
        refreshItems();  // Actualiza la tabla con los datos del inventario

        // ===== CONFIGURACIÓN DE LA TABLA =====
        // Crea la tabla principal que mostrará los productos
        TableView<ProductVM> table = new TableView<>(items);  // Conecta la tabla con la lista observable
        
        // Configura la columna de nombre
        TableColumn<ProductVM, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));  // Conecta con la propiedad "name"
        
        // Configura la columna de precio
        TableColumn<ProductVM, Number> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));  // Conecta con la propiedad "price"
        
        // Configura la columna de cantidad
        TableColumn<ProductVM, Number> qtyCol = new TableColumn<>("Cantidad");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));  // Conecta con la propiedad "quantity"
        
        // Agrega todas las columnas a la tabla
        table.getColumns().addAll(nameCol, priceCol, qtyCol);
        
        // Establece la política de redimensionamiento de columnas
        // FLEX_LAST_COLUMN hace que la última columna se expanda para llenar el espacio disponible
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // ===== CONFIGURACIÓN DEL FORMULARIO =====
        // Crea los campos de entrada para agregar/editar productos
        TextField nameField = new TextField(); 
        nameField.setPromptText("Nombre");  // Texto de ayuda que aparece cuando está vacío
        
        TextField priceField = new TextField(); 
        priceField.setPromptText("Precio");  // Texto de ayuda que aparece cuando está vacío
        
        TextField qtyField = new TextField(); 
        qtyField.setPromptText("Cantidad");  // Texto de ayuda que aparece cuando está vacío
        
        // Crea los botones de acción
        Button saveBtn = new Button("Guardar");    // Para agregar/actualizar productos
        Button removeBtn = new Button("Eliminar"); // Para eliminar productos

        // Organiza los controles en una fila horizontal con espaciado de 8 píxeles
        HBox form = new HBox(8, nameField, priceField, qtyField, saveBtn, removeBtn);
        form.setAlignment(Pos.CENTER_LEFT);  // Alinea los elementos a la izquierda

        // ===== EVENTOS DE LOS BOTONES =====
        // Evento del botón "Guardar" - Agrega o actualiza un producto
        saveBtn.setOnAction(e -> {
            // Obtiene los valores de los campos de texto
            String name = nameField.getText().trim();  // trim() elimina espacios al inicio y final
            double price = parseDoubleLocal(priceField.getText().trim(), 0);  // Convierte a número
            int qty = parseIntLocal(qtyField.getText().trim(), 0);  // Convierte a entero
            
            // Valida que el nombre no esté vacío
            if (name.isEmpty()) { 
                showInfo("Nombre requerido");  // Muestra mensaje de error
                return;  // Sale del método sin hacer nada más
            }
            
            // Agrega o actualiza el producto en el inventario
            inventory.addOrUpdate(name, price, qty);
            refreshItems();  // Actualiza la tabla para mostrar los cambios
        });

        // Evento del botón "Eliminar" - Elimina un producto
        removeBtn.setOnAction(e -> {
            // Obtiene el nombre del campo de texto
            String name = nameField.getText().trim();
            
            // Valida que el nombre no esté vacío
            if (name.isEmpty()) { 
                showInfo("Nombre requerido");  // Muestra mensaje de error
                return;  // Sale del método sin hacer nada más
            }
            
            // Intenta eliminar el producto del inventario
            boolean ok = inventory.remove(name);
            if (!ok) 
                showInfo("No encontrado");  // Si no se encontró, muestra mensaje
            refreshItems();  // Actualiza la tabla para mostrar los cambios
        });

        // ===== CONFIGURACIÓN DE DEEPLINKS =====
        // Campo de texto para ingresar URIs de deeplink
        TextField uriField = new TextField(); 
        uriField.setPromptText("quickbite://add?name=...&price=...&qty=...");  // Texto de ayuda con ejemplo
        
        Button openUriBtn = new Button("Abrir deeplink");  // Botón para procesar la URI
        
        // Organiza los controles de deeplink en una fila horizontal
        HBox linkBox = new HBox(8, uriField, openUriBtn);
        linkBox.setAlignment(Pos.CENTER_LEFT);  // Alinea a la izquierda

        // Evento del botón de deeplink - Procesa la URI ingresada
        openUriBtn.setOnAction(e -> {
            String uri = uriField.getText().trim();  // Obtiene la URI del campo de texto
            String result = router.handle(uri);      // Procesa la URI con el router
            showInfo(result);                        // Muestra el resultado en un diálogo
            refreshItems();                          // Actualiza la tabla por si hubo cambios
        });

        // Configura el callback para cuando se solicite mostrar el inventario desde un deeplink
        router.setOnInventoryViewRequested(() -> Platform.runLater(stage::toFront));

        // ===== CONFIGURACIÓN DEL LAYOUT PRINCIPAL =====
        // Crea un Pane para posicionamiento libre
        Pane root = new Pane();
        
        // Crear elementos
        // Crear contenedor para el título con fondo
        Pane titleContainer = new Pane();
        titleContainer.setPrefSize(190, 500);
        titleContainer.getStyleClass().add("title-container");
        
        // Crear el header
        HBox headerBox = header("QuickBite FX");
        titleContainer.getChildren().add(headerBox);
        
        // Centrar el header dentro del contenedor
        headerBox.setLayoutX(60 - headerBox.getBoundsInLocal().getWidth() / 2);
        headerBox.setLayoutY(20 - headerBox.getBoundsInLocal().getHeight() / 2);
        VBox leftPanel = new VBox(12, form);
        VBox bottomPanel = new VBox(12, new Separator(), new Text("Deeplink"), linkBox);
        
        // Posicionar elementos libremente
        titleContainer.setLayoutX(0);    // 20px desde la izquierda
        titleContainer.setLayoutY(0);    // 20px desde arriba
        
        leftPanel.setLayoutX(200);    // 20px desde la izquierda
        leftPanel.setLayoutY(20);    // 20px desde arriba
        
        table.setLayoutX(200);       // 400px desde la izquierda
        table.setLayoutY(60);         // 50px desde arriba
        table.setPrefWidth(600);     // Ancho de 400px
        table.setPrefHeight(320);    // Alto de 300px
        
        bottomPanel.setLayoutX(200);  // 20px desde la izquierda
        bottomPanel.setLayoutY(400);
        bottomPanel.setPrefWidth(900); 
        
        // Agregar elementos al Pane
        root.getChildren().addAll(titleContainer, leftPanel, table, bottomPanel);
        
        /*VBox root = new VBox(12,
                header("QuickBite FX"),  // Título de la aplicación
                form,                    // Formulario de entrada
                table,                   // Tabla de productos
                new Separator(),         // Línea separadora
                new Text("Deeplink"),    // Etiqueta para la sección de deeplinks
                linkBox                  // Controles de deeplink
        );*/
        root.setPadding(new Insets(16));  // Márgenes de 16 píxeles en todos los lados
        root.getStyleClass().add("root-pane");  // Aplica la clase CSS "root-pane"

        // ===== CONFIGURACIÓN DE LA ESCENA Y VENTANA =====
        // Crea la escena principal con el layout y dimensiones específicas
        Scene scene = new Scene(root, 900, 500);  // Ancho: 900px, Alto: 500px
        
        // Carga el archivo CSS para aplicar estilos personalizados
        java.net.URL cssUrl = QuickBiteFX.class.getResource("/quickbite.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: quickbite.css no encontrado en src/main/resources");
        }

        // Configura la ventana principal
        stage.setTitle("QuickBite FX");  // Título de la ventana
        stage.setScene(scene);           // Asigna la escena a la ventana
        stage.show();                    // Muestra la ventana

        // ===== PROCESAMIENTO DE ARGUMENTOS DE LÍNEA DE COMANDOS =====
        // Si se pasaron argumentos al ejecutar la aplicación, los procesa como deeplinks
        List<String> args = getParameters().getUnnamed();  // Obtiene los argumentos sin nombre
        if (args != null) {
            for (String a : args) {
                String res = router.handle(a);  // Procesa cada argumento como deeplink
                System.out.println("Deeplink: " + a + " => " + res);  // Imprime el resultado en consola
            }
            refreshItems();  // Actualiza la tabla por si hubo cambios
        }
    }

    // ===== MÉTODOS AUXILIARES =====
    
    /**
     * Actualiza la lista observable con todos los productos del inventario
     * Este método se llama cada vez que se modifica el inventario para mantener
     * la tabla sincronizada con los datos.
     */
    private void refreshItems() {
        items.setAll(inventory.all());  // Reemplaza todos los elementos de la lista observable
    }

    /**
     * Convierte un string a double de forma segura (versión local)
     * @param s String a convertir
     * @param fb Valor por defecto si la conversión falla
     * @return El número convertido o el valor por defecto
     */
    private static double parseDoubleLocal(String s, double fb) {
        if (s == null || s.isEmpty()) 
            return fb;  // Si está vacío, devuelve el valor por defecto
        try { 
            return Double.parseDouble(s); 
        } catch (NumberFormatException e) { 
            return fb;  // Si no es un número válido, devuelve el valor por defecto
        }
    }
    
    /**
     * Convierte un string a int de forma segura (versión local)
     * @param s String a convertir
     * @param fb Valor por defecto si la conversión falla
     * @return El número convertido o el valor por defecto
     */
    private static int parseIntLocal(String s, int fb) {
        if (s == null || s.isEmpty()) 
            return fb;  // Si está vacío, devuelve el valor por defecto
        try { 
            return Integer.parseInt(s); 
        } catch (NumberFormatException e) { 
            return fb;  // Si no es un número válido, devuelve el valor por defecto
        }
    }

    /**
     * Crea un encabezado estilizado para la aplicación
     * @param title Texto del título
     * @return Contenedor HBox con el título estilizado
     */
    private static HBox header(String title) {
        Text t = new Text(title);  // Crea el texto del título
        t.getStyleClass().add("title");  // Aplica la clase CSS "title" para estilizado
        
        HBox box = new HBox(t);  // Envuelve el texto en un contenedor horizontal
        box.setAlignment(Pos.CENTER_LEFT);  // Alinea el texto a la izquierda
        return box;
    }

    /**
     * Muestra un diálogo de información al usuario
     * @param msg Mensaje a mostrar en el diálogo
     */
    private static void showInfo(String msg) {
        // Crea un diálogo de tipo información
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);  // No muestra encabezado
        alert.setTitle("QuickBite FX");  // Título del diálogo
        alert.showAndWait();  // Muestra el diálogo y espera a que el usuario lo cierre
    }

    /**
     * PUNTO DE ENTRADA DE LA APLICACIÓN
     * 
     * Este método se ejecuta cuando se inicia la aplicación desde la línea de comandos.
     * La llamada a launch() inicia el ciclo de vida de JavaFX y eventualmente
     * ejecuta el método start().
     * 
     * @param args Argumentos de línea de comandos (pueden ser deeplinks)
     */
    // si la clase extiende javafx.application.Application, usa este main:
    public static void main(String[] args) {
        // Esto inicia la aplicación JavaFX
        javafx.application.Application.launch(QuickBiteFX.class, args);
    }
}
