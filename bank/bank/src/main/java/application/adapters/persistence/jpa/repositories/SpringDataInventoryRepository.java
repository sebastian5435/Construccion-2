package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryRepository extends JpaRepository<InventoryEntity, String> {

    Optional<InventoryEntity> findByProductIdAndWarehouseId(String productId, String warehouseId);

    List<InventoryEntity> findByProductId(String productId);

    List<InventoryEntity> findByWarehouseId(String warehouseId);
}
