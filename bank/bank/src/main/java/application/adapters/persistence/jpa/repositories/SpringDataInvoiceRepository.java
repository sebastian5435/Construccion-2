package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InvoiceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    Optional<InvoiceEntity> findByOrderId(String orderId);
}
