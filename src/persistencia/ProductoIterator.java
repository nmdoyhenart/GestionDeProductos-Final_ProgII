package persistencia;
import modelo.Producto;
import java.util.Iterator;
import java.util.List;

public class ProductoIterator implements Iterator<Producto> {
    private List<Producto> productos;
    private int posicion = 0;

    public ProductoIterator(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public boolean hasNext() {
        while (posicion < productos.size()) { // si hay mas elementos y su stock es mayor a 0
            if (productos.get(posicion).getStock() > 0) {
                return true;
            }
            posicion++; // salteas los que tienen stock 0
        }
        return false;
    }

    @Override
    public Producto next() {
        return productos.get(posicion++); // producto actual y avanza la posición
    }
}