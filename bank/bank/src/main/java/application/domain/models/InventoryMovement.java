package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import java.time.LocalDateTime;

public class InventoryMovement {
    private String id;
    private Inventory inventory;
    private InventoryMovementType type;
    private int quantity;
    private User performedBy;
    private String reference;
    private LocalDateTime occurredAt;

    public InventoryMovement() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    public InventoryMovementType getType() { return type; }
    public void setType(InventoryMovementType type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
