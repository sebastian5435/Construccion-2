package application.modelo;

public class Bodega {
    private String id;
    private String nombre;
    private String tipoBodega; 

    public Bodega() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoBodega() { return tipoBodega; }
    public void setTipoBodega(String tipoBodega) { this.tipoBodega = tipoBodega; }
}
