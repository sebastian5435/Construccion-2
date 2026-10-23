package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataInventoryMovementRepository;
import application.domain.models.InventoryMovement;
import application.domain.ports.output.InventoryMovementRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaInventoryMovementRepositoryAdapter implements InventoryMovementRepositoryPort {

    private final SpringDataInventoryMovementRepository repository;
    private final JpaDomainMapper mapper;

    public JpaInventoryMovementRepositoryAdapter(SpringDataInventoryMovementRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        if (movement.getId() == null) {
            movement.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(movement));
        return movement;
    }

    @Override
    public List<InventoryMovement> findByInventoryId(String inventoryId) {
        return repository.findByInventoryIdOrderByOccurredAtAsc(inventoryId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<InventoryMovement> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
