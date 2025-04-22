package modelo;
import excepciones.CaracteresExcedidos;

public abstract class Producto implements Comparable<Producto> {
    protected String nombre;
    protected double precio;
    protected int stock;
    
    // Constructor
    public Producto(String nombre, double precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Métodos abstractos
    public abstract String mostrarInfo();
    
    @Override
    public int compareTo(Producto otro) {
        return this.nombre.compareTo(otro.getNombre()); // Ordena por nombre de producto
    }
    
    @Override
    public String toString() {
    return mostrarInfo();
    }
    
    // Getters
    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }
    
    // Setters
    public void setNombre(String nombre) throws CaracteresExcedidos {
        if (nombre.length() > 20) {
            throw new CaracteresExcedidos("El nombre no puede tener más de 20 caracteres.");
        }
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}