package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataReturnRepository;
import application.domain.models.Return;
import application.domain.ports.output.ReturnRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaReturnRepositoryAdapter implements ReturnRepositoryPort {

    private final SpringDataReturnRepository repository;
    private final JpaDomainMapper mapper;

    public JpaReturnRepositoryAdapter(SpringDataReturnRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Return save(Return returnProcess) {
        if (returnProcess.getId() == null) {
            returnProcess.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(returnProcess));
        return returnProcess;
    }

    @Override
    public Optional<Return> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Return> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomain);
    }

    @Override
    public List<Return> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
