package modelo;
import java.util.Comparator;

public class ProductoComparatorStock implements Comparator<Producto> {
    @Override
    public int compare(Producto prod1, Producto prod2) {
        return Integer.compare(prod1.getStock(), prod2.getStock()); // Ordena por stock
    }
}