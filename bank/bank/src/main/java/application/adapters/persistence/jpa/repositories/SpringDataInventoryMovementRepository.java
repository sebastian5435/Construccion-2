package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryMovementRepository extends JpaRepository<InventoryMovementEntity, String> {

    List<InventoryMovementEntity> findByInventoryIdOrderByOccurredAtAsc(String inventoryId);
}
