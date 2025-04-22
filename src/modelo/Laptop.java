package modelo;

public class Laptop extends Producto implements Conectividad {
    private double tamanioPantalla; // en pulgadas
    private String tieneTarjetaGrafica;
    
    // Constructor
    public Laptop(String nombre, double precio, double tamanioPantalla, String tieneTarjetaGrafica) {
        super(nombre, precio, 1);
        validarGrafica(tieneTarjetaGrafica);
        validarPantalla(tamanioPantalla);
        this.tamanioPantalla = (tamanioPantalla == 0) ? 22 : tamanioPantalla;
        this.tieneTarjetaGrafica = tieneTarjetaGrafica;
    }

    // Constructor sin pantalla (usa 15.6 por defecto)
    public Laptop(String nombre, double precio, String tieneTarjetaGrafica) {
        this(nombre, precio, 15.6, tieneTarjetaGrafica);
    }

    // Constructor solo con nombre y precio
    public Laptop(String nombre, double precio) {
        this(nombre, precio, 15.6, "no");
    }
    
    // Método abstracto
    @Override
    public String mostrarInfo() {
        return "Modelo: " + nombre + "\nPrecio: " + precio + "\nStock: " + stock + "\nTamaño de la pantalla: " + tamanioPantalla + "\nTarjeta grafica: " + tieneTarjetaGrafica;
    }
    
    @Override
    public String verificarConexion() {
        return tieneTarjetaGrafica.equalsIgnoreCase("si") ? "Listo para tareas gráficas" : "No apto para gaming";
    }
 
    // Método
    private void validarGrafica(String valor) {
    String lower = valor.toLowerCase();
    if (!lower.equals("si") && !lower.equals("no")) {
        throw new IllegalArgumentException("Respuesta inválida.");
        }
    }
    
    private void validarPantalla(double valor) {
    if (valor < 10 || valor > 50) {
        throw new IllegalArgumentException("El tamaño de pantalla debe estar entre 10 y 50 pulgadas.");
        }
    }
    
    // Getters
    public double getTamanioPantalla() {
        return tamanioPantalla;
    }

    public String isTieneTarjetaGrafica() {
        return tieneTarjetaGrafica;
    }
    
    // Setters
    public void setTamanioPantalla(double tamanioPantalla) {
        this.tamanioPantalla = tamanioPantalla;
    }

    public void setTieneTarjetaGrafica(String tieneTarjetaGrafica) {
        this.tieneTarjetaGrafica = tieneTarjetaGrafica;
    }
}