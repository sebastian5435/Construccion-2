package application.domain.ports.output;

import application.domain.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String identifier);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean existsByEmail(String email);
    boolean existsByIdentifier(String identifier);
    void deleteById(String identifier);
}
