package application.domain.models;
import application.domain.valueobjects.OrderStatus;
import java.util.List;

public class Order {
    private String id;
    private Buyer buyer;
    private List<Product> products;
    private OrderStatus status; 
    public Order() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}