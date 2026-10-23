package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataShipmentRepository;
import application.domain.models.Shipment;
import application.domain.ports.output.ShipmentRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final SpringDataShipmentRepository repository;
    private final JpaDomainMapper mapper;

    public JpaShipmentRepositoryAdapter(SpringDataShipmentRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Shipment save(Shipment shipment) {
        repository.save(mapper.toEntity(shipment));
        return shipment;
    }

    @Override
    public Optional<Shipment> findByTrackingId(String trackingId) {
        return repository.findById(trackingId).map(mapper::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomain);
    }

    @Override
    public List<Shipment> findByOperatorId(String operatorId) {
        return repository.findByLogisticsOperatorIdentifier(operatorId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Shipment> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByTrackingId(String trackingId) {
        repository.deleteById(trackingId);
    }
}
