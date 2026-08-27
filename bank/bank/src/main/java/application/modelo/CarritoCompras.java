package application.modelo;
import java.util.List;

public class CarritoCompras {
    private Comprador comprador;
    private List<Producto> productosSeleccionados;

    public CarritoCompras() {}

    public Comprador getComprador() { return comprador; }
    public void setComprador(Comprador comprador) { this.comprador = comprador; }

    public List<Producto> getProductosSeleccionados() { return productosSeleccionados; }
    public void setProductosSeleccionados(List<Producto> productosSeleccionados) { this.productosSeleccionados = productosSeleccionados; }
}
