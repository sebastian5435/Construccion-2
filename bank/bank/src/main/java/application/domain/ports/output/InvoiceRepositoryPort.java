package application.domain.ports.output;

import application.domain.models.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(String id);
    Optional<Invoice> findByOrderId(String orderId);
    List<Invoice> findAll();
    void deleteById(String id);
}
