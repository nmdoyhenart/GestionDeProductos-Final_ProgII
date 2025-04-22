package vista;
import excepciones.CampoVacio;
import excepciones.CaracteresExcedidos;
import excepciones.NoCeroOMenor;
import javafx.application.Application; 
import javafx.collections.FXCollections; // Utilidad para crear listas observables
import javafx.collections.ObservableList; // Lista que permite detectar cambios en la interfaz
import javafx.geometry.Pos; // Permite manejar la alineación de elementos en layouts
import javafx.scene.Scene;
import javafx.scene.control.*; // Importa todos los controles gráficos (TextField, Button, etc)
import javafx.scene.layout.VBox; // Layout vertical para organizar elementos en columna
import javafx.stage.FileChooser; // Permite elegir archivos desde el explorador
import javafx.stage.Stage;
import modelo.*; 
import persistencia.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List; 
import javafx.scene.layout.HBox;

public class Main extends Application { 
    private GestorProductos gestor; 
    private ObservableList<Producto> productosObservable;
    private ListView<String> listViewProductos; 
    
    @Override
    public void start(Stage primaryStage) {
    gestor = new GestorProductos();
    productosObservable = FXCollections.observableArrayList();
    listViewProductos = new ListView<>();
    
    // Campos principales
    TextField nombreField = new TextField();
    nombreField.setPromptText("Nombre del producto");

    TextField precioField = new TextField();
    precioField.setPromptText("Precio del producto ($USD)");

    TextField stockField = new TextField();
    stockField.setPromptText("Stock del producto");

    ComboBox<String> tipoProductoCombo = new ComboBox<>();
    tipoProductoCombo.setItems(FXCollections.observableArrayList("Celular", "Laptop", "Periféricos"));
    tipoProductoCombo.setPromptText("Tipo de Producto");

    // Campos adicionales
    TextField sistemaOperativoField = new TextField();
    sistemaOperativoField.setPromptText("Sistema operativo");

    ComboBox<String> senialComboBox = new ComboBox<>();
    senialComboBox.setItems(FXCollections.observableArrayList("4G", "5G"));
    senialComboBox.setPromptText("Señal (4G/5G)");

    TextField tamañoPantallaField = new TextField();
    tamañoPantallaField.setPromptText("Tamaño de la pantalla (En pulgadas)");

    TextField tieneGraficaField = new TextField();
    tieneGraficaField.setPromptText("Tarjeta Gráfica (Si/No)");

    TextField tipoField = new TextField();
    tipoField.setPromptText("Tipo de périferico (teclado, mouse o auriculares)");

    ComboBox<String> resistenteComboBox = new ComboBox<>();
    resistenteComboBox.setItems(FXCollections.observableArrayList("Sí", "No"));
    resistenteComboBox.setPromptText("Water proof");

    VBox camposExtra = new VBox(10);
    tipoProductoCombo.setOnAction(e -> {
        camposExtra.getChildren().clear();
        switch (tipoProductoCombo.getValue()) {
            case "Celular" -> camposExtra.getChildren().addAll(sistemaOperativoField, senialComboBox);
            case "Laptop" -> camposExtra.getChildren().addAll(tamañoPantallaField, tieneGraficaField);
            case "Periféricos" -> camposExtra.getChildren().addAll(tipoField, resistenteComboBox);
        }
    });
    
    // Función para autocompletado
    listViewProductos.setOnMouseClicked(e -> {
        int index = listViewProductos.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            Producto seleccionado = gestor.listar().get(index);
            // Campos comunes
            nombreField.setText(seleccionado.getNombre());
            precioField.setText(String.valueOf(seleccionado.getPrecio()));
            stockField.setText(String.valueOf(seleccionado.getStock()));
            
            // Rellenar segun corresponda
            if (seleccionado instanceof Celular celular) {
                tipoProductoCombo.setValue("Celular");
                camposExtra.getChildren().setAll(sistemaOperativoField, senialComboBox);
                sistemaOperativoField.setText(celular.getSistemaOp());
                senialComboBox.setValue(celular.getSenialCaptada());
            } else if (seleccionado instanceof Laptop laptop) {
                
                tipoProductoCombo.setValue("Laptop");
                camposExtra.getChildren().setAll(tamañoPantallaField, tieneGraficaField);
                tamañoPantallaField.setText(String.valueOf(laptop.getTamanioPantalla()));
                tieneGraficaField.setText(laptop.isTieneTarjetaGrafica());
                
            } else if (seleccionado instanceof Perifericos periferico) {
                tipoProductoCombo.setValue("Periféricos");
                camposExtra.getChildren().setAll(tipoField, resistenteComboBox);
                tipoField.setText(periferico.getTipo().name());
                resistenteComboBox.setValue(periferico.getResistenteAgua());
            }
        }
    });
    
    // Botones
    Button nuevoProductoBtn = new Button("Nuevo Producto");
    Button agregarBtn = new Button("Agregar");
    Button editarBtn = new Button("Editar");
    Button eliminarBtn = new Button("Eliminar");
    Button aumentarPreciosBtn = new Button("↑ Precios 10%");

    // Acciones
    agregarBtn.setOnAction(e -> agregarProducto(nombreField, precioField, stockField, tipoProductoCombo,
            sistemaOperativoField, senialComboBox, tamañoPantallaField, tieneGraficaField, tipoField, resistenteComboBox));

    editarBtn.setOnAction(e -> editarProducto(nombreField, precioField, stockField, tipoProductoCombo,
            sistemaOperativoField, senialComboBox, tamañoPantallaField, tieneGraficaField, tipoField, resistenteComboBox));

    eliminarBtn.setOnAction(e -> eliminarProducto());

    aumentarPreciosBtn.setOnAction(e -> {
        gestor.aplicarCambios(p -> p.setPrecio(p.getPrecio() * 1.10));
        actualizarListView();
    });

    nuevoProductoBtn.setOnAction(e -> {
        nombreField.clear();
        precioField.clear();
        stockField.clear();
        tipoProductoCombo.setValue(null);

        sistemaOperativoField.clear();
        senialComboBox.setValue(null);
        tamañoPantallaField.clear();
        tieneGraficaField.clear();
        tipoField.clear();
        resistenteComboBox.setValue(null);
        camposExtra.getChildren().clear();

        // Deseleccionar productos
        listViewProductos.getSelectionModel().clearSelection();
        });

    HBox botonesAcciones = new HBox(10, nuevoProductoBtn, agregarBtn, editarBtn, eliminarBtn, aumentarPreciosBtn);
    botonesAcciones.setAlignment(Pos.CENTER);

    Button ordenarPorPrecioAscBtn = new Button("Ordenar por precio (menor a mayor)");
    Button ordenarPorPrecioDescBtn = new Button("Ordenar por precio (mayor a menor)");
    Button filtrarCelularesBtn = new Button("Filtrar Celulares");
    Button filtrarLaptopsBtn = new Button("Filtrar Laptops");
    Button filtrarProductosBtn = new Button("Filtrar Productos");
    Button ordenOriginalBtn = new Button("Orden original");
    Button ordenStockBtn = new Button("Ordenar por stock (menor a mayor)");
    //Button mostrarProdStockBtn = new Button("Con stock > 0 (positivo)");

    ordenarPorPrecioAscBtn.setOnAction(e -> {
        List<Producto> ordenados = new ArrayList<>(gestor.listar());
        ordenados.sort(new ProductoComparatorPrecio());
        actualizarListView(ordenados);
    });
    
    ordenarPorPrecioDescBtn.setOnAction(e -> {
    List<Producto> ordenados = new ArrayList<>(gestor.listar());
    ordenados.sort(new ProductoComparatorPrecio().reversed());
    actualizarListView(ordenados);
    });
    
    ordenStockBtn.setOnAction(e -> {
    List<Producto> ordenados = new ArrayList<>(gestor.listar());
    ordenados.sort(new ProductoComparatorStock());
    actualizarListView(ordenados);
    });
    
    filtrarCelularesBtn.setOnAction(e -> actualizarListView(gestor.filtrarPorTipo(Celular.class)));
    filtrarLaptopsBtn.setOnAction(e -> actualizarListView(gestor.filtrarPorTipo(Laptop.class)));
    filtrarProductosBtn.setOnAction(e -> actualizarListView(gestor.filtrarPorTipo(Perifericos.class)));
    ordenOriginalBtn.setOnAction(e -> actualizarListView(gestor.filtrarPorTipo(Producto.class)));
    
    //mostrarProdStockBtn.setOnAction(e -> {
    //List<String> productosConStock = new ArrayList<>();
    //Iterator<Producto> it = gestor.getIteratorConStock();
    //while (it.hasNext()) {
       // productosConStock.add(it.next().toString());
    //}
    //listViewProductos.setItems(FXCollections.observableArrayList(productosConStock));
    //});
    
    HBox botonesFiltrado = new HBox(10, ordenarPorPrecioAscBtn, ordenarPorPrecioDescBtn, ordenOriginalBtn,ordenStockBtn, filtrarCelularesBtn, filtrarLaptopsBtn, filtrarProductosBtn);
    botonesFiltrado.setAlignment(Pos.CENTER);
    
    Button guardarCSVBtn = new Button("Guardar CSV");
    Button guardarJSONBtn = new Button("Guardar JSON");
    Button guardarTXTBtn =new Button("Guardar TXT");
    Button cargarCSVBtn = new Button("Cargar CSV");
    Button cargarJSONBtn = new Button("Cargar JSON");

    guardarCSVBtn.setOnAction(e -> guardarDatos("CSV", primaryStage));
    guardarJSONBtn.setOnAction(e -> guardarDatos("JSON", primaryStage));
    guardarTXTBtn.setOnAction(e -> guardarDatos("TXT", primaryStage));
    cargarCSVBtn.setOnAction(e -> cargarDatos("CSV", primaryStage));
    cargarJSONBtn.setOnAction(e -> cargarDatos("JSON", primaryStage));
    
    HBox botonesPersistencia = new HBox(10, guardarCSVBtn, guardarJSONBtn, guardarTXTBtn,cargarCSVBtn, cargarJSONBtn);
    botonesPersistencia.setAlignment(Pos.CENTER);

    VBox layoutInterno = new VBox(10,
            nombreField, precioField, stockField, tipoProductoCombo,
            camposExtra, botonesAcciones, botonesFiltrado, botonesPersistencia, listViewProductos
    );
    layoutInterno.setAlignment(Pos.CENTER);
    layoutInterno.setPrefWidth(400);

    ScrollPane scrollPane = new ScrollPane(layoutInterno);
    scrollPane.setFitToWidth(true);
    
    Scene scene = new Scene(scrollPane, 600, 800);
    primaryStage.setTitle("Gestión de productos");
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true); // Pantalla completa
    primaryStage.show();
    }

    public void validarCampos(String nombre, double precio, int stock)
        throws CampoVacio, CaracteresExcedidos, NoCeroOMenor {

    if (nombre == null || nombre.trim().isEmpty()) {
        throw new CampoVacio("El nombre no puede estar vacío.");
    }
    if (nombre.length() > 30) {
        throw new CaracteresExcedidos("El nombre no puede superar los 20 caracteres.");
    }
    if (precio <= 0) {
        throw new NoCeroOMenor("El precio debe ser mayor a 0.");
    }
    if (stock < 0) {
        throw new NoCeroOMenor("El stock no puede ser negativo.");
     }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
    Alert alerta = new Alert(Alert.AlertType.ERROR);
    alerta.setTitle(titulo);
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);
    alerta.showAndWait();
    }
    
    private void agregarProducto(TextField nombreField, TextField precioField, TextField stockField,
                             ComboBox<String> tipoProductoCombo, TextField sistemaOperativoField, ComboBox<String> senialComboBox,
                             TextField tamañoPantallaField, TextField tieneGraficaField,
                             TextField tipoField, ComboBox<String> resistententeComboBox) {

    try {
        String nombre = nombreField.getText();
        String precioStr = precioField.getText();
        String stockStr = stockField.getText();
        String tipoProducto = tipoProductoCombo.getValue();

        if (tipoProducto == null || tipoProducto.isEmpty()) {
            throw new excepciones.CampoVacio("Debe seleccionar un tipo de producto.");
        }

        double precio = Double.parseDouble(precioStr);
        int stock = Integer.parseInt(stockStr);

        // Validación con método reutilizable
        validarCampos(nombre, precio, stock);

        Producto producto = null;

        switch (tipoProducto) {
        case "Celular" -> {
            String sistema = sistemaOperativoField.getText();
            String senial = senialComboBox.getValue();
            
            if (sistema.isEmpty() || senial.isEmpty()) {
                throw new excepciones.CampoVacio("Los campos de sistema operativo y señal no pueden estar vacíos.");
            }
            producto = new Celular(nombre, precio, stock, sistema, senial);
        }
        
        case "Laptop" -> {
            String tamaño = tamañoPantallaField.getText();
            String tieneGrafica = tieneGraficaField.getText();
            if (tamaño.isEmpty() || tieneGrafica.isEmpty()) {
                throw new excepciones.CampoVacio("Los campos de tamaño de pantalla y gráfica no pueden estar vacíos.");
            }
            double pantalla = Double.parseDouble(tamaño);
            producto = new Laptop(nombre, precio, pantalla, tieneGrafica);
        }
        
        case "Periféricos" -> {
                String tipoTexto = tipoField.getText().toUpperCase(); // para que no falle por minúsculas

                CategoriaProducto tipoEnum;
                try {
                    tipoEnum = CategoriaProducto.valueOf(tipoTexto);
                } catch (IllegalArgumentException ex) {
                    throw new excepciones.CampoVacio("Tipo de periférico inválido. Usá TECLADO, MOUSE o AURICULARES.");
                }
                
            String resist = resistententeComboBox.getValue();
   
            if (tipoTexto.isEmpty() || resist == null) {
                throw new excepciones.CampoVacio("Los campos de tipo y resistencia no pueden estar vacíos.");
            }
            producto = new Perifericos(nombre, precio, stock, tipoEnum, resist);
            }
        }

        if (producto != null) {
            gestor.agregarProductoGenerico(gestor.listar(), producto);
            actualizarListView();

            // Limpiar campos
            nombreField.clear();
            precioField.clear();
            stockField.clear();
            tipoProductoCombo.setValue(null);
            sistemaOperativoField.clear();
            senialComboBox.setValue(null);
            tamañoPantallaField.clear();
            tieneGraficaField.clear();
            tipoField.clear();
            resistententeComboBox.setValue(null);
        }

    } catch (excepciones.CampoVacio | excepciones.CaracteresExcedidos | excepciones.NoCeroOMenor e) {
        mostrarAlerta("Error de validación", e.getMessage());

    } catch (NumberFormatException e) {
        mostrarAlerta("Error de formato", "Precio y stock deben ser números válidos.");
        }
    }
     
   private void editarProducto(TextField nombreField, TextField precioField, TextField stockField,
                            ComboBox<String> tipoProductoCombo, TextField sistemaOperativoField, ComboBox<String> senialComboBox,
                            TextField tamañoPantallaField, TextField tieneGraficaField,
                            TextField tipoField, ComboBox<String> resistenteComboBox) {

    int index = listViewProductos.getSelectionModel().getSelectedIndex();
    if (index < 0) {
        mostrarAlerta("Sin selección", "Seleccioná un producto de la lista para editar.");
        return;
    }

    try {
        String nombre = nombreField.getText();
        String precioStr = precioField.getText();
        String stockStr = stockField.getText();
        String tipoProducto = tipoProductoCombo.getValue();

        if (tipoProducto == null || tipoProducto.isEmpty()) {
            throw new excepciones.CampoVacio("Debe seleccionar un tipo de producto.");
        }

        double precio = Double.parseDouble(precioStr);
        int stock = Integer.parseInt(stockStr);

        validarCampos(nombre, precio, stock);

        Producto productoEditado = null;

        switch (tipoProducto) {
            case "Celular" -> {
                String sistema = sistemaOperativoField.getText();
                String senial = senialComboBox.getValue();
                
                if (sistema.isEmpty() || senial == null) {
                    throw new excepciones.CampoVacio("Los campos de sistema operativo y señal no pueden estar vacíos.");
                }
                productoEditado = new Celular(nombre, precio, stock, sistema, senial);
            }
            
            case "Laptop" -> {
                String tamaño = tamañoPantallaField.getText();
                String grafica = tieneGraficaField.getText();
                if (tamaño.isEmpty() || grafica.isEmpty()) {
                    throw new excepciones.CampoVacio("Los campos de tamaño de pantalla y gráfica no pueden estar vacíos.");
                }
                double pantalla = Double.parseDouble(tamaño);
                productoEditado = new Laptop(nombre, precio, pantalla, grafica);
            }
            
            case "Periféricos" -> {
               String tipoTexto = tipoField.getText().toUpperCase(); // para que no falle por minúsculas

                CategoriaProducto tipoEnum;
                try {
                    tipoEnum = CategoriaProducto.valueOf(tipoTexto);
                } catch (IllegalArgumentException ex) {
                    throw new excepciones.CampoVacio("Tipo de periférico inválido. Usá TECLADO, MOUSE o AURICULARES.");
                }
                
                String resistente = resistenteComboBox.getValue();
                if (tipoTexto.isEmpty() || resistente == null) {
                    throw new excepciones.CampoVacio("Los campos de tipo y resistencia no pueden estar vacíos.");
                }
                productoEditado = new Perifericos(nombre, precio, stock, tipoEnum, resistente);
            }
        }

        if (productoEditado != null) {
            gestor.actualizar(index, productoEditado);
            actualizarListView();
        }

    } catch (excepciones.CampoVacio | excepciones.CaracteresExcedidos | excepciones.NoCeroOMenor e) {
        mostrarAlerta("Error de validación", e.getMessage());

    } catch (NumberFormatException e) {
        mostrarAlerta("Error de formato", "Precio, stock o pantalla deben ser números válidos.");
        }   
    }
    
    private void actualizarListView(List<? extends Producto> productos) {
    productosObservable.clear();
    productosObservable.addAll(productos);
    listViewProductos.setItems(FXCollections.observableArrayList(
        productosObservable.stream().map(Producto::toString).toList()));
    }
    
    // Muestra todos los productos cargados
    private void actualizarListView() {
    actualizarListView(gestor.listar()); // Reutiliza la versión con parámetro
    }

    private void guardarDatos(String formato, Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Guardar Archivo");
    // Filtro según formato
    switch (formato) {
        case "CSV" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        case "JSON" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo JSON", "*.json"));
        case "TXT" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo TXT","*.txt"));
    }

    File file = fileChooser.showSaveDialog(primaryStage);
    if (file != null) {
        String path = file.getPath();
        switch (formato) {
            case "CSV" -> gestor.guardarEnCSV(path);
            case "JSON" -> gestor.guardarEnJSON(path);
            case "TXT" -> gestor.guardarEnTXT(path);
            }
        }
     }
    
    private void cargarDatos(String formato, Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Cargar Archivo");
    // Filtrar por formato
    switch (formato) {
        case "CSV" -> fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        case "JSON" -> fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo JSON", "*.json"));
    }

    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
        String path = file.getPath();
        switch (formato) {
            case "CSV" -> gestor.cargarDesdeCSV(path);
            case "JSON" -> gestor.cargarDesdeJSON(path);
        }
        actualizarListView(); // Refresca la vista
        }
    }
    
    private void eliminarProducto() {
    int seleccion = listViewProductos.getSelectionModel().getSelectedIndex();
    if (seleccion >= 0) {
        gestor.eliminar(seleccion);
        actualizarListView();
    } else {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccioná un producto para eliminar.", ButtonType.OK);
        alert.showAndWait();
        }   
    }

    public static void main(String[] args) {
        launch(args);
    }
}