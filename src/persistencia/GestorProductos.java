package persistencia;
import modelo.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import modelo.Celular;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import modelo.CategoriaProducto;
import modelo.Laptop;
import modelo.Perifericos;

public class GestorProductos implements GestorCRUD<Producto> {
    private List<Producto> listaProductos;

    public GestorProductos() {
        this.listaProductos = new ArrayList<>();
    }
    
    @Override
    public void agregar(Producto producto) {
        listaProductos.add(producto);
        System.out.println("Producto agregado: " + producto.getNombre());
    }

    @Override
    public List<Producto> listar() {
        return listaProductos;
    }
    
    @Override
    public void actualizar(int index, Producto producto) {
        if (index >= 0 && index < listaProductos.size()) {
            listaProductos.set(index, producto);
            System.out.println("Producto actualizado: " + producto.getNombre());
        } else {
            System.out.println("No se encontro el producto.");
        }
    }
    
    @Override
    public void eliminar(int index) {
        if (index >= 0 && index < listaProductos.size()) {
            Producto eliminado = listaProductos.remove(index);
            System.out.println("Producto eliminado: " + eliminado.getNombre());
        } else {
            System.out.println("No se encontro el producto.");
        }
    }
  
    public <T extends Producto> List<T> filtrarPorTipo(Class<T> tipo) {
    List<T> productosFiltrados = new ArrayList<>();
        for (Producto producto : listaProductos) {
            if (tipo.isInstance(producto)) { // si es un producto
                productosFiltrados.add(tipo.cast(producto)); // lo añado a la lista
            }
        }
        return productosFiltrados; // devuelvo la lista luego de las comparaciones
    }
    
    public void guardarEnCSV(String archivo) {
    try (PrintWriter escribir = new PrintWriter(new FileWriter(archivo))) {
        escribir.println("Tipo,Nombre,Precio,Stock,Sistema,Señal,Pantalla,Grafica,TipoPeriferico,Resistencia");

        for (Producto producto : listaProductos) {
            String linea = "";

            if (producto instanceof Celular celular) {
                linea = String.format("Celular,%s,%.2f,%d,%s,%s,,,,",
                        celular.getNombre(),
                        celular.getPrecio(),
                        celular.getStock(),
                        celular.getSistemaOp(),
                        celular.getSenialCaptada());

            } else if (producto instanceof Laptop laptop) {
                linea = String.format("Laptop,%s,%.2f,%d,,,%.1f,%s,,",
                        laptop.getNombre(),
                        laptop.getPrecio(),
                        laptop.getStock(),
                        laptop.getTamanioPantalla(),
                        laptop.isTieneTarjetaGrafica());

            } else if (producto instanceof Perifericos perif) {
                linea = String.format("Periferico,%s,%.2f,%d,,,,,%s,%s",
                        perif.getNombre(),
                        perif.getPrecio(),
                        perif.getStock(),
                        perif.getTipo().name(),
                        perif.getResistenteAgua());
            }
            escribir.println(linea);
        }
        System.out.println("Datos guardados en archivo CSV.");
    } catch (IOException e) {
        e.printStackTrace();
        }   
    }

    public void cargarDesdeCSV(String archivo) {
    listaProductos.clear();
    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        String linea;
        int lineaActual = 0;

        while ((linea = br.readLine()) != null) {
            lineaActual++;

            if (lineaActual == 1) continue; // Saltear encabezado

            String[] datos = linea.split(",", -1); // Incluye los campos vacíos

            if (datos.length >= 10) {
                String tipo = datos[0].trim(); // elimina los espacios en blanco al principio y al final de una cadena de texto
                String nombre = datos[1].trim();
                double precio = Double.parseDouble(datos[2].trim());
                int stock = Integer.parseInt(datos[3].trim());

                Producto producto = switch (tipo) {
                    case "Celular" -> new Celular(nombre, precio, stock, datos[4].trim(), datos[5].trim());
                    case "Laptop" -> new Laptop(nombre, precio, Double.parseDouble(datos[6].trim()), datos[7].trim());
                    case "Periferico" -> new Perifericos(nombre, precio, stock,
                            CategoriaProducto.valueOf(datos[8].trim().toUpperCase()), datos[9].trim());
                    default -> null;
                };
                if (producto != null) listaProductos.add(producto);
            }
        }
        System.out.println("Datos cargados desde archivo CSV.");
    } catch (IOException e) {
        e.printStackTrace();
        }
    }

    public void guardarEnJSON(String archivo) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    try (FileWriter escribir = new FileWriter(archivo)) {
        JsonArray lista = new JsonArray(); // Creación de un arreglo JSON

        for (Producto producto : listaProductos) {
            JsonObject obj = gson.toJsonTree(producto).getAsJsonObject(); // Conversión del producto a un objeto JSON

            if (producto instanceof Celular) {
                obj.addProperty("tipo", "Celular");
            } else if (producto instanceof Laptop) {
               obj.addProperty("tipo", "Laptop");
            } else if (producto instanceof Perifericos) {
                obj.addProperty("tipo", "Periferico");
            }
            lista.add(obj);
        }
        gson.toJson(lista, escribir);
        System.out.println("Datos guardados en archivo JSON.");
    } catch (IOException e) {
        e.printStackTrace();
        }
    }

   public void cargarDesdeJSON(String archivo) {
    Gson gson = new Gson(); // Creación de un objeto Gson para deserializar

    try (FileReader lector = new FileReader(archivo)) {
        JsonArray lista = JsonParser.parseReader(lector).getAsJsonArray(); // Leemos el contenido del archivo como un arreglo JSON
        listaProductos.clear(); // Limpiamos la lista por las dudas

        for (JsonElement e : lista) {
            JsonObject obj = e.getAsJsonObject(); // Transformación a objeto JSON
            String tipo = obj.get("tipo").getAsString(); // Lectura y reconocimiento del campo "tipo"

            Producto producto = null;

            switch (tipo) {
                case "Celular" -> producto = gson.fromJson(obj, Celular.class);
                case "Laptop" -> producto = gson.fromJson(obj, Laptop.class);
                case "Periferico" -> producto = gson.fromJson(obj, Perifericos.class);
            }

            if (producto != null) listaProductos.add(producto);
        }
        System.out.println("Datos cargados desde archivo JSON.");
    } catch (IOException e) {
        e.printStackTrace();
        }
    }
    
    public void guardarEnTXT(String archivo) {
    try (PrintWriter escribir = new PrintWriter(new FileWriter(archivo))) {
        escribir.println("===LISTADO DE PRODUCTOS===");
        escribir.println();

        escribir.printf("%-15s %-10s %-8s %-50s%n", "Nombre", "Precio", "Stock", "Detalles"); // Encabezados alineados como tabla
        escribir.println("=".repeat(90)); // Separador visual

        for (Producto p : listaProductos) {
            escribir.printf("%-15s $%-9.2f %-8d %-50s%n", // Mostrar los datos en columnas, reemplazando saltos de línea del detalle por " | "
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock(),
                    p.mostrarInfo().replaceAll("\\n", " | ") // Remplaza los saltos de linea
            );
        }

        System.out.println("Datos guardados en archivo TXT");
    } catch (IOException e) {
        e.printStackTrace();
        }
    }

    public Iterator<Producto> getIteratorConStock() {
    return new ProductoIterator(listaProductos);
    }
    
   public void aplicarCambios(Consumer<Producto> consumer) {
   for (Producto p : listaProductos) {
         consumer.accept(p);
        }
    }
    
    public void agregarProductoGenerico(List<? super Producto> lista, Producto producto) { // Wildcard ? super 
    lista.add(producto);
    System.out.println("Producto agregado (lista génerica): " + producto.getNombre());
    }
}