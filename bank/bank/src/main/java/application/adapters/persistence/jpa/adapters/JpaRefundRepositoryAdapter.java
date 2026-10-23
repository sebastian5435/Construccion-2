package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataRefundRepository;
import application.domain.models.Refund;
import application.domain.ports.output.RefundRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaRefundRepositoryAdapter implements RefundRepositoryPort {

    private final SpringDataRefundRepository repository;
    private final JpaDomainMapper mapper;

    public JpaRefundRepositoryAdapter(SpringDataRefundRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Refund save(Refund refund) {
        if (refund.getId() == null) {
            refund.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(refund));
        return refund;
    }

    @Override
    public Optional<Refund> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Refund> findByReturnId(String returnId) {
        return repository.findByReturnProcessId(returnId).map(mapper::toDomain);
    }

    @Override
    public List<Refund> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
