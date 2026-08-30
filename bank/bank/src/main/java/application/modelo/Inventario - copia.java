package application.modelo;

public class Inventario {
    private Producto producto;
    private Bodega bodega;
    private int existencias;

    public Inventario() {}

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Bodega getBodega() { return bodega; }
    public void setBodega(Bodega bodega) { this.bodega = bodega; }

    public int getExistencias() { return existencias; }
    
    // Validación crítica solicitada en el documento
    public void setExistencias(int existencias) { 
        if(existencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas bajo ninguna circunstancia");
        }
        this.existencias = existencias; 
    }
}
