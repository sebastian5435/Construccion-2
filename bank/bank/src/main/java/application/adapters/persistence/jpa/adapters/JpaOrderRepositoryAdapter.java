package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataOrderRepository;
import application.domain.models.Order;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;
    private final JpaDomainMapper mapper;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(order));
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByBuyerId(String buyerId) {
        return repository.findByBuyerIdentifier(buyerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return repository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
