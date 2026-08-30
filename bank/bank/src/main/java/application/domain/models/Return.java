package application.domain.models;

public class Return {
    private String id;
    private Order order;
    public Return() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}