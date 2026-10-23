package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ShipmentEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataShipmentRepository extends JpaRepository<ShipmentEntity, String> {

    Optional<ShipmentEntity> findByOrderId(String orderId);

    List<ShipmentEntity> findByLogisticsOperatorIdentifier(String operatorId);
}
