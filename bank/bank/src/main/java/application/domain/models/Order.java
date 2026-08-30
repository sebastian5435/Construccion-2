package application.domain.models;
import java.util.List;

public class Order {
    private String id;
    private Buyer buyer;
    private List<Product> products;
    private String status; 

    public Order() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
