package application.adapters.persistence.inmemory;

import application.domain.models.Invoice;
import application.domain.ports.output.InvoiceRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryInvoiceRepository implements InvoiceRepositoryPort {

    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice.getId() == null) {
            invoice.setId(UUID.randomUUID().toString());
        }
        invoices.put(invoice.getId(), invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return Optional.ofNullable(invoices.get(id));
    }

    @Override
    public Optional<Invoice> findByOrderId(String orderId) {
        return invoices.values().stream()
                .filter(i -> i.getOrder() != null
                        && orderId.equals(i.getOrder().getId()))
                .findFirst();
    }

    @Override
    public List<Invoice> findAll() {
        return new ArrayList<>(invoices.values());
    }

    @Override
    public void deleteById(String id) {
        invoices.remove(id);
    }
}
