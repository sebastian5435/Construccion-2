package application.domain.ports.input;

import application.domain.models.Inventory;
import java.util.List;

/**
 * Manages distributed inventory across warehouses.
 * Enforces the invariant that inventory can never be negative.
 * Covers business objective OBJ-06.
 */
public interface InventoryManagementUseCase {

    Inventory registerInventory(String productId, String warehouseId, int initialQuantity);

    Inventory addStock(String productId, String warehouseId, int quantity);

    Inventory reserveStock(String productId, String warehouseId, int quantity);

    Inventory releaseReservedStock(String productId, String warehouseId, int quantity);

    Inventory consumeReservedStock(String productId, String warehouseId, int quantity);

    Inventory adjustStock(String productId, String warehouseId, int newQuantity);

    Inventory returnStock(String productId, String warehouseId, int quantity);

    int checkAvailability(String productId, String warehouseId);

    List<Inventory> listInventoryByProduct(String productId);

    List<Inventory> listInventoryByWarehouse(String warehouseId);
}
