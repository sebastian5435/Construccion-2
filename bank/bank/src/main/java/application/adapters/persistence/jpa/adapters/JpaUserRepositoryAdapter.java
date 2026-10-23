package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataUserRepository;
import application.domain.models.User;
import application.domain.ports.output.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;
    private final JpaDomainMapper mapper;

    public JpaUserRepositoryAdapter(SpringDataUserRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        repository.save(mapper.toEntity(user));
        return user;
    }

    @Override
    public Optional<User> findById(String identifier) {
        return repository.findById(identifier).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByIdentifier(String identifier) {
        return repository.existsById(identifier);
    }

    @Override
    public void deleteById(String identifier) {
        repository.deleteById(identifier);
    }
}
