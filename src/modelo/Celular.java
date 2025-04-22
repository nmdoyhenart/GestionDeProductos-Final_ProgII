package modelo;

public class Celular extends Producto implements Conectividad {
    private String sistemaOp;
    private String senialCaptada;
    
    // Constructor
    public Celular(String nombre, double precio, int stock, String sistemaOp, String senialCaptada) {
        super(nombre, precio, stock);
        validarSenial(senialCaptada);
        this.sistemaOp = sistemaOp;
        this.senialCaptada = senialCaptada;
    }

    // Constructor con un parámetro menos (sin señal, por defecto "no")
    public Celular(String nombre, double precio, int stock, String sistemaOp) {
        this(nombre, precio, stock, sistemaOp, "4G");
    }

    // Constructor con solo nombre y precio (sistemaOp y señal por defecto)
    public Celular(String nombre, double precio, int stock) {
        this(nombre, precio, stock, "Iphone", "4G");
    }
    
    // Método abstracto
    @Override
    public String mostrarInfo() {
        return  "Modelo: " + nombre +"\nPrecio: " + precio + "\nStock: " + stock + "\nSistema operativo: " + sistemaOp + "\nSeñal: " + senialCaptada;
    }
    
    @Override
    public String verificarConexion() {
        return senialCaptada.equalsIgnoreCase("si") ? "Conectado a red 5G" : "Sin señal";
    }
    
    // Método
    private void validarSenial(String senialCaptada) {
    String senialLower = senialCaptada.toLowerCase();
    if (!senialLower.equals("5g") && !senialLower.equals("4g")) {
         throw new IllegalArgumentException("Respuesta inválida.");
        }
    }
    
    // Getters
    public String getSistemaOp() {
        return sistemaOp;
    }
    
    public String getSenialCaptada(){
        return senialCaptada;
    }
    
    // Setters
    public void setSistemaOp(String sistemaOp) {
        this.sistemaOp = sistemaOp;
    }
    
    public void setSenialCaptada(String senialCaptada){
        this.senialCaptada = senialCaptada;
    }
}