package application.domain.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private String id;
    private Buyer buyer;
    private List<OrderItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
