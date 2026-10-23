package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataInventoryRepository;
import application.domain.models.Inventory;
import application.domain.ports.output.InventoryRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaInventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final SpringDataInventoryRepository repository;
    private final JpaDomainMapper mapper;

    public JpaInventoryRepositoryAdapter(SpringDataInventoryRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            inventory.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(inventory));
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
        return repository.findByProductIdAndWarehouseId(productId, warehouseId).map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findByProduct(String productId) {
        return repository.findByProductId(productId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findByWarehouse(String warehouseId) {
        return repository.findByWarehouseId(warehouseId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
