package application.domain.ports.output;

import application.domain.models.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(String id);
    Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId);
    List<Inventory> findByProduct(String productId);
    List<Inventory> findByWarehouse(String warehouseId);
    List<Inventory> findAll();
    void deleteById(String id);
}
