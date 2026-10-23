package application.domain.ports.input;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import java.util.List;

/**
 * Manages distributed inventory across warehouses.
 * Enforces the invariant that inventory can never be negative.
 * Reservations, sale exits and returns are triggered automatically by the
 * order, shipment and return flows; this port exposes the manual operations
 * performed by Sellers and Logistics Operators.
 * Covers business objective OBJ-06.
 */
public interface InventoryManagementUseCase {

    Inventory registerInventory(String productId, String warehouseId, int initialQuantity, String requesterId);

    Inventory addStock(String productId, String warehouseId, int quantity, String requesterId);

    Inventory adjustStock(String productId, String warehouseId, int newQuantity, String requesterId);

    Inventory markAsDamaged(String productId, String warehouseId, String requesterId);

    Inventory markAsAvailable(String productId, String warehouseId, String requesterId);

    int checkAvailability(String productId, String warehouseId);

    List<Inventory> listInventoryByProduct(String productId);

    List<Inventory> listInventoryByWarehouse(String warehouseId);

    List<InventoryMovement> listMovements(String productId, String warehouseId);
}
