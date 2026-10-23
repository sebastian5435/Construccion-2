package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderEntity;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findByBuyerIdentifier(String buyerId);

    List<OrderEntity> findByStatus(OrderStatus status);
}
