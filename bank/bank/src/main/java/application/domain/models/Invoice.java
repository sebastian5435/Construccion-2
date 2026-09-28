package application.domain.models;

import java.time.LocalDateTime;

public class Invoice {
    private String id;
    private Order order;
    private double totalAmount;
    private LocalDateTime issueDate;

    public Invoice() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
}
