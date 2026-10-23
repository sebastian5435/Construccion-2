package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataWarehouseRepository;
import application.domain.models.Warehouse;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaWarehouseRepositoryAdapter implements WarehouseRepositoryPort {

    private final SpringDataWarehouseRepository repository;
    private final JpaDomainMapper mapper;

    public JpaWarehouseRepositoryAdapter(SpringDataWarehouseRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            warehouse.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(warehouse));
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Warehouse> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Warehouse> findByType(WarehouseType type) {
        return repository.findByWarehouseType(type).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Warehouse> findBySellerId(String sellerId) {
        return repository.findBySellerIdentifier(sellerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
