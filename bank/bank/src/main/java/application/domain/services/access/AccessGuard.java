package application.domain.services.access;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.User;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.SystemRole;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AccessGuard {

    private final UserRepositoryPort userRepository;

    public AccessGuard(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User requireActiveUser(String userId, String action, SystemRole... allowedRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(user::hasRole);
        if (!allowed) {
            String roles = Arrays.stream(allowedRoles)
                    .map(SystemRole::name)
                    .collect(Collectors.joining(" or "));
            throw new UnauthorizedRoleException(action, roles);
        }
        if (!user.isActive()) {
            throw new InvalidOperationException("User " + userId + " is not active.");
        }
        return user;
    }
}
