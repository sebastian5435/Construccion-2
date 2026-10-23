package application.domain.models;

import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.Map;

public class OperationalReport {
    private LocalDateTime generatedAt;
    private Map<SystemRole, Long> usersByRole;
    private Map<ProductStatus, Long> productsByStatus;
    private Map<OrderStatus, Long> ordersByStatus;
    private double totalInvoiced;
    private double totalRefunded;
    private long pendingReturns;
    private long damagedInventoryRecords;

    public OperationalReport() {}

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public Map<SystemRole, Long> getUsersByRole() { return usersByRole; }
    public void setUsersByRole(Map<SystemRole, Long> usersByRole) { this.usersByRole = usersByRole; }

    public Map<ProductStatus, Long> getProductsByStatus() { return productsByStatus; }
    public void setProductsByStatus(Map<ProductStatus, Long> productsByStatus) { this.productsByStatus = productsByStatus; }

    public Map<OrderStatus, Long> getOrdersByStatus() { return ordersByStatus; }
    public void setOrdersByStatus(Map<OrderStatus, Long> ordersByStatus) { this.ordersByStatus = ordersByStatus; }

    public double getTotalInvoiced() { return totalInvoiced; }
    public void setTotalInvoiced(double totalInvoiced) { this.totalInvoiced = totalInvoiced; }

    public double getTotalRefunded() { return totalRefunded; }
    public void setTotalRefunded(double totalRefunded) { this.totalRefunded = totalRefunded; }

    public long getPendingReturns() { return pendingReturns; }
    public void setPendingReturns(long pendingReturns) { this.pendingReturns = pendingReturns; }

    public long getDamagedInventoryRecords() { return damagedInventoryRecords; }
    public void setDamagedInventoryRecords(long damagedInventoryRecords) { this.damagedInventoryRecords = damagedInventoryRecords; }
}
