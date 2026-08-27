package application.modelo;
import java.util.List;

public class Producto {
    private String id;
    private String tipoProducto; // Fisico o Digital
    private List<String> variantes;
    private String estado; // Publicado, Suspendido o Descontinuado
    private Vendedor vendedor;

    public Producto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(String tipoProducto) { this.tipoProducto = tipoProducto; }

    public List<String> getVariantes() { return variantes; }
    public void setVariantes(List<String> variantes) { this.variantes = variantes; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }
}
