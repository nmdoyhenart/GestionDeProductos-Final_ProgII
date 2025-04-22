package excepciones;

public class NoCeroOMenor extends Exception {
    public NoCeroOMenor(String mensaje){
        super(mensaje);
    }
}