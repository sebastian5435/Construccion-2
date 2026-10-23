package application.domain.models;

import application.domain.exceptions.InvalidOrderStateTransitionException;
import application.domain.exceptions.OrderAlreadyFinalizedException;
import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private Buyer buyer;
    private List<OrderItem> items;
    private OrderStatus status;
    private String deliveryAddress;
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

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isFinalized() {
        return status != null && status.isFinal();
    }

    public boolean isPlacedBy(String buyerId) {
        return buyer != null && buyer.getIdentifier().equals(buyerId);
    }

    public boolean requiresShipping() {
        return items.stream().anyMatch(OrderItem::requiresShipping);
    }

    public List<OrderItem> getPhysicalItems() {
        return items.stream().filter(OrderItem::requiresShipping).toList();
    }

    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void markAsPendingPayment() {
        changeStatus(OrderStatus.PENDING_PAYMENT);
    }

    public void markAsPaid() {
        changeStatus(OrderStatus.PAID);
    }

    public void markAsDispatched() {
        changeStatus(OrderStatus.DISPATCHED);
    }

    public void markAsDelivered() {
        if (status == OrderStatus.PAID && !requiresShipping()) {
            applyStatus(OrderStatus.DELIVERED);
            return;
        }
        changeStatus(OrderStatus.DELIVERED);
    }

    public void cancel() {
        changeStatus(OrderStatus.CANCELLED);
    }

    private void changeStatus(OrderStatus target) {
        if (isFinalized()) {
            throw new OrderAlreadyFinalizedException(id);
        }
        if (!status.canTransitionTo(target)) {
            throw new InvalidOrderStateTransitionException(status, target);
        }
        applyStatus(target);
    }

    private void applyStatus(OrderStatus target) {
        this.status = target;
        this.updatedAt = LocalDateTime.now();
    }
}
