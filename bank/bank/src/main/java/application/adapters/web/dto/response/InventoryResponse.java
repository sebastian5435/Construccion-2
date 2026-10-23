package application.adapters.web.dto.response;

import application.domain.models.Inventory;
import application.domain.valueobjects.InventoryStatus;

public record InventoryResponse(
        String id,
        String productId,
        String productName,
        String warehouseId,
        String warehouseName,
        int quantity,
        int reservedQuantity,
        int availableQuantity,
        InventoryStatus status) {

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(inventory.getId(),
                inventory.getProduct().getId(), inventory.getProduct().getName(),
                inventory.getWarehouse().getId(), inventory.getWarehouse().getName(),
                inventory.getQuantity(), inventory.getReservedQuantity(),
                inventory.getAvailableQuantity(), inventory.getStatus());
    }
}
