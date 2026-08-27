package application.modelo;

public class Factura {
    private String id;
    private Pedido pedido;
    private double valorTotal;

    public Factura() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}
