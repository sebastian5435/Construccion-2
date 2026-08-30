package application.domain.models;

public class Shipment {
    private String trackingId;
    private Order order;
    private String logisticsOperator;

    public Shipment() {}

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getLogisticsOperator() { return logisticsOperator; }
    public void setLogisticsOperator(String logisticsOperator) { this.logisticsOperator = logisticsOperator; }
}
