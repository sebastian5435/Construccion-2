package application.adapters.persistence.inmemory;

import application.domain.models.User;
import application.domain.ports.output.UserRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryUserRepository implements UserRepositoryPort {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getIdentifier(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String identifier) {
        return Optional.ofNullable(users.get(identifier));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(u -> email.equals(u.getEmail()));
    }

    @Override
    public boolean existsByIdentifier(String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public void deleteById(String identifier) {
        users.remove(identifier);
    }
}
