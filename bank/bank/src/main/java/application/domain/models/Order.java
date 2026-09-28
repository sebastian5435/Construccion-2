package application.domain.models;

import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private Buyer buyer;
    private List<OrderItem> items;
    private OrderStatus status;
    private double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.items = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isFinalized() {
        return status == OrderStatus.DELIVERED;
    }

    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
