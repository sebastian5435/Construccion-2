package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.RefundEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRefundRepository extends JpaRepository<RefundEntity, String> {

    Optional<RefundEntity> findByReturnProcessId(String returnId);
}
