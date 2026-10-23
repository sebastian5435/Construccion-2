package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataInvoiceRepository;
import application.domain.models.Invoice;
import application.domain.ports.output.InvoiceRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaInvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final SpringDataInvoiceRepository repository;
    private final JpaDomainMapper mapper;

    public JpaInvoiceRepositoryAdapter(SpringDataInvoiceRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice.getId() == null) {
            invoice.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(invoice));
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
