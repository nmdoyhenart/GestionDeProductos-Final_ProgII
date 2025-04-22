package excepciones;

public class CaracteresExcedidos extends Exception {
    public CaracteresExcedidos(String mensaje){
        super(mensaje);
    }
}