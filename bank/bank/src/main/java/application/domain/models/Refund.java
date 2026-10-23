package application.domain.models;

import java.time.LocalDateTime;

public class Refund {
    private String id;
    private Return returnProcess;
    private double amount;
    private LocalDateTime issuedAt;

    public Refund() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Return getReturnProcess() { return returnProcess; }
    public void setReturnProcess(Return returnProcess) { this.returnProcess = returnProcess; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
