package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ReturnEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReturnRepository extends JpaRepository<ReturnEntity, String> {

    Optional<ReturnEntity> findByOrderId(String orderId);
}
