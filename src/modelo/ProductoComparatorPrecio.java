package modelo;
import java.util.Comparator;

public class ProductoComparatorPrecio implements Comparator<Producto> {
    @Override
    public int compare(Producto prod1, Producto prod2) {
        return Double.compare(prod1.getPrecio(), prod2.getPrecio()); // Ordena por precio
    }
}