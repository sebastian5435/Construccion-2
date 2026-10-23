package application.adapters.persistence.inmemory;

import application.domain.models.InventoryMovement;
import application.domain.ports.output.InventoryMovementRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryInventoryMovementRepository implements InventoryMovementRepositoryPort {

    private final List<InventoryMovement> movements = new CopyOnWriteArrayList<>();

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        if (movement.getId() == null) {
            movement.setId(UUID.randomUUID().toString());
        }
        movements.add(movement);
        return movement;
    }

    @Override
    public List<InventoryMovement> findByInventoryId(String inventoryId) {
        return movements.stream()
                .filter(m -> m.getInventory() != null
                        && inventoryId.equals(m.getInventory().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryMovement> findAll() {
        return new ArrayList<>(movements);
    }
}
