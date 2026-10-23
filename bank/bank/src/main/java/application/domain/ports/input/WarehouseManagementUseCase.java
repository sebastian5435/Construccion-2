package application.domain.ports.input;

import application.domain.models.Warehouse;
import java.util.List;

/**
 * Manages the registration and administration of warehouses.
 * Covers business objective OBJ-04.
 */
public interface WarehouseManagementUseCase {

    Warehouse registerMarketplaceWarehouse(Warehouse warehouse, String administratorId);

    Warehouse registerSellerWarehouse(Warehouse warehouse, String sellerId, String administratorId);

    Warehouse findWarehouseById(String warehouseId);

    List<Warehouse> listMarketplaceWarehouses();

    List<Warehouse> listWarehousesBySeller(String sellerId);

    List<Warehouse> listAllWarehouses();
}
