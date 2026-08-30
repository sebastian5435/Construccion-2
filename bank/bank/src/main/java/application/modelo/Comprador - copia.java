package application.modelo;
import java.util.List;

public class Comprador extends Usuario {
    private String direccionPrincipal;
    private List<String> direccionesAdicionales;
    private String estadoComercial;

    public Comprador() {}

    public String getDireccionPrincipal() { return direccionPrincipal; }
    public void setDireccionPrincipal(String direccionPrincipal) { this.direccionPrincipal = direccionPrincipal; }

    public List<String> getDireccionesAdicionales() { return direccionesAdicionales; }
    public void setDireccionesAdicionales(List<String> direccionesAdicionales) { this.direccionesAdicionales = direccionesAdicionales; }

    public String getEstadoComercial() { return estadoComercial; }
    public void setEstadoComercial(String estadoComercial) { this.estadoComercial = estadoComercial; }
}
