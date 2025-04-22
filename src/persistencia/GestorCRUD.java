package persistencia;
import java.util.List;

public interface GestorCRUD<T> {
    void agregar(T entidad);
    List<T> listar();
    void actualizar(int index, T entidad);
    void eliminar(int index);
}