package application.domain.models;

import java.time.LocalDateTime;

public class Shipment {
    private String trackingId;
    private Order order;
    private LogisticsOperator logisticsOperator;
    private String destinationAddress;
    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;

    public Shipment() {}

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LogisticsOperator getLogisticsOperator() { return logisticsOperator; }
    public void setLogisticsOperator(LogisticsOperator logisticsOperator) { this.logisticsOperator = logisticsOperator; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(LocalDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public boolean isAssignedTo(String operatorId) {
        return logisticsOperator != null && logisticsOperator.getIdentifier().equals(operatorId);
    }

    public void dispatch() {
        order.markAsDispatched();
        this.dispatchedAt = LocalDateTime.now();
    }

    public void confirmDelivery() {
        order.markAsDelivered();
        this.deliveredAt = LocalDateTime.now();
    }
}
