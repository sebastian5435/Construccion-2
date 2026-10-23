package application.adapters.web.dto.response;

import application.domain.models.InventoryMovement;
import application.domain.valueobjects.InventoryMovementType;
import java.time.LocalDateTime;

public record InventoryMovementResponse(
        String id,
        InventoryMovementType type,
        int quantity,
        String performedBy,
        String reference,
        LocalDateTime occurredAt) {

    public static InventoryMovementResponse from(InventoryMovement movement) {
        return new InventoryMovementResponse(movement.getId(), movement.getType(), movement.getQuantity(),
                movement.getPerformedBy() == null ? null : movement.getPerformedBy().getIdentifier(),
                movement.getReference(), movement.getOccurredAt());
    }
}
