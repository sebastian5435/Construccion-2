package application.domain.models;

import application.domain.exceptions.InvalidOperationException;
import application.domain.valueobjects.ReturnStatus;
import java.time.LocalDateTime;

public class Return {
    private String id;
    private Order order;
    private String reason;
    private ReturnStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private Administrator resolvedBy;

    public Return() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnStatus status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Administrator getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(Administrator resolvedBy) { this.resolvedBy = resolvedBy; }

    public void approve(Administrator administrator) {
        resolve(ReturnStatus.APPROVED, administrator);
    }

    public void reject(Administrator administrator) {
        resolve(ReturnStatus.REJECTED, administrator);
    }

    private void resolve(ReturnStatus target, Administrator administrator) {
        if (status != ReturnStatus.REQUESTED) {
            throw new InvalidOperationException(
                    "Return " + id + " has already been resolved as " + status + ".");
        }
        this.status = target;
        this.resolvedBy = administrator;
        this.resolvedAt = LocalDateTime.now();
    }
}
