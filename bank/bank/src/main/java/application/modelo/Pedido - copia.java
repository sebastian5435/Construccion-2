package application.modelo;
import java.util.List;

public class Pedido {
    private String id;
    private Comprador comprador;
    private List<Producto> productos;
    private String estado; // Carrito, Pendiente de Pago, Pagado, Despachado, Entregado, Finalizado

    public Pedido() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Comprador getComprador() { return comprador; }
    public void setComprador(Comprador comprador) { this.comprador = comprador; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
