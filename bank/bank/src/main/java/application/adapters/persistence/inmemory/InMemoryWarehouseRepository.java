package application.adapters.persistence.inmemory;

import application.domain.models.Warehouse;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.valueobjects.WarehouseType;
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
public class InMemoryWarehouseRepository implements WarehouseRepositoryPort {

    private final Map<String, Warehouse> warehouses = new ConcurrentHashMap<>();

    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            warehouse.setId(UUID.randomUUID().toString());
        }
        warehouses.put(warehouse.getId(), warehouse);
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(String id) {
        return Optional.ofNullable(warehouses.get(id));
    }

    @Override
    public List<Warehouse> findAll() {
        return new ArrayList<>(warehouses.values());
    }

    @Override
    public List<Warehouse> findByType(WarehouseType type) {
        return warehouses.values().stream()
                .filter(w -> w.getWarehouseType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Warehouse> findBySellerId(String sellerId) {
        return warehouses.values().stream()
                .filter(w -> w.getSeller() != null
                        && sellerId.equals(w.getSeller().getIdentifier()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        warehouses.remove(id);
    }
}
