package application.modelo;

public class Envio {
    private String idTracking;
    private Pedido pedido;
    private String operadorLogistico;

    public Envio() {}

    public String getIdTracking() { return idTracking; }
    public void setIdTracking(String idTracking) { this.idTracking = idTracking; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public String getOperadorLogistico() { return operadorLogistico; }
    public void setOperadorLogistico(String operadorLogistico) { this.operadorLogistico = operadorLogistico; }
}
