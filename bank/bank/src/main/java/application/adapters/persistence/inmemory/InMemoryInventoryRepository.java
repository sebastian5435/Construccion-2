package application.adapters.persistence.inmemory;

import application.domain.models.Inventory;
import application.domain.ports.output.InventoryRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryInventoryRepository implements InventoryRepositoryPort {

    private final Map<String, Inventory> inventories = new ConcurrentHashMap<>();

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            inventory.setId(UUID.randomUUID().toString());
        }
        inventories.put(inventory.getId(), inventory);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(String id) {
        return Optional.ofNullable(inventories.get(id));
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
        return inventories.values().stream()
                .filter(i -> i.getProduct() != null
                        && productId.equals(i.getProduct().getId())
                        && i.getWarehouse() != null
                        && warehouseId.equals(i.getWarehouse().getId()))
                .findFirst();
    }

    @Override
    public List<Inventory> findByProduct(String productId) {
        return inventories.values().stream()
                .filter(i -> i.getProduct() != null
                        && productId.equals(i.getProduct().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByWarehouse(String warehouseId) {
        return inventories.values().stream()
                .filter(i -> i.getWarehouse() != null
                        && warehouseId.equals(i.getWarehouse().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findAll() {
        return new ArrayList<>(inventories.values());
    }

    @Override
    public void deleteById(String id) {
        inventories.remove(id);
    }
}
