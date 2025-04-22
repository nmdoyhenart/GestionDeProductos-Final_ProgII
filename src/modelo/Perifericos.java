package modelo;

public class Perifericos extends Producto {
    private CategoriaProducto tipo; // Teclado, mouse, auriculares
    private String resistenteAgua;
    
    // Constructor
    public Perifericos(String nombre, double precio, int stock, CategoriaProducto tipo, String resistenteAgua) {
        super(nombre, precio, stock);
        validarResistencia(resistenteAgua);
        this.tipo = tipo;
        this.resistenteAgua = resistenteAgua;
    }
    
    // Constructor sin resistencia
    public Perifericos(String nombre, double precio, int stock, CategoriaProducto tipo) {
        this(nombre, precio, stock, tipo, "no");
    }

    // Constructor básico
    public Perifericos(String nombre, double precio, int stock) {
        this(nombre, precio, stock, CategoriaProducto.MOUSE, "no");
    }

    // Método abstracto
    @Override
    public String mostrarInfo() {
       return "Périferico: " + nombre + "\nPrecio: " + precio + "\nStock: " + stock + "\nTipo: " + tipo + "\nResistente al agua: " + resistenteAgua;
    }
    
    // Método
    private void validarResistencia(String valor) {
    String lower = valor.toLowerCase();
    if (!lower.equals("si") && !lower.equals("no")) {
         throw new IllegalArgumentException("Respuesta inválida.");
        }
    }
    
    // Getter
    public CategoriaProducto getTipo() {
      return tipo;
    }
    
    public String getResistenteAgua(){
        return resistenteAgua;
    }
    
    // Setter
    public void setResistenteAgua(String resistenteAgua){
        this.resistenteAgua = resistenteAgua;
    }
}