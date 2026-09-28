package application.domain.services.user;

import application.domain.exceptions.DuplicateEmailException;
import application.domain.exceptions.DuplicateIdentifierException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.ports.input.UserManagementUseCase;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserManagementUseCase {

    private final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Buyer registerBuyer(Buyer buyer) {
        validateNewUser(buyer);
        buyer.setRole(SystemRole.BUYER);
        buyer.setStatus(UserStatus.ACTIVE);
        if (buyer.getCommercialStatus() == null) {
            buyer.setCommercialStatus(CommercialStatus.ENABLED);
        }
        return (Buyer) userRepository.save(buyer);
    }

    @Override
    public Seller registerSeller(Seller seller, String administratorId) {
        requireAdministrator(administratorId, "registerSeller");
        validateNewUser(seller);
        seller.setRole(SystemRole.SELLER);
        seller.setStatus(UserStatus.ACTIVE);
        return (Seller) userRepository.save(seller);
    }

    @Override
    public LogisticsOperator registerLogisticsOperator(LogisticsOperator operator, String administratorId) {
        requireAdministrator(administratorId, "registerLogisticsOperator");
        validateNewUser(operator);
        operator.setRole(SystemRole.LOGISTICS_OPERATOR);
        operator.setStatus(UserStatus.ACTIVE);
        return (LogisticsOperator) userRepository.save(operator);
    }

    @Override
    public Supervisor registerSupervisor(Supervisor supervisor, String administratorId) {
        requireAdministrator(administratorId, "registerSupervisor");
        validateNewUser(supervisor);
        supervisor.setRole(SystemRole.SUPERVISOR);
        supervisor.setStatus(UserStatus.ACTIVE);
        return (Supervisor) userRepository.save(supervisor);
    }

    @Override
    public Administrator registerAdministrator(Administrator administrator, String requesterAdministratorId) {
        requireAdministrator(requesterAdministratorId, "registerAdministrator");
        validateNewUser(administrator);
        administrator.setRole(SystemRole.ADMINISTRATOR);
        administrator.setStatus(UserStatus.ACTIVE);
        return (Administrator) userRepository.save(administrator);
    }

    @Override
    public User blockUser(String userId, String administratorId) {
        requireAdministrator(administratorId, "blockUser");
        User user = findUserById(userId);
        user.setStatus(UserStatus.BLOCKED);
        return userRepository.save(user);
    }

    @Override
    public User activateUser(String userId, String administratorId) {
        requireAdministrator(administratorId, "activateUser");
        User user = findUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email));
    }

    @Override
    public List<User> listAllUsers(String administratorId) {
        requireAdministrator(administratorId, "listAllUsers");
        return userRepository.findAll();
    }

    private void validateNewUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidOperationException("Email is required.");
        }
        if (user.getIdentifier() == null || user.getIdentifier().isBlank()) {
            throw new InvalidOperationException("Identifier is required.");
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            throw new InvalidOperationException("Full name is required.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
        if (userRepository.existsByIdentifier(user.getIdentifier())) {
            throw new DuplicateIdentifierException(user.getIdentifier());
        }
    }

    private void requireAdministrator(String userId, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != SystemRole.ADMINISTRATOR) {
            throw new UnauthorizedRoleException(action, SystemRole.ADMINISTRATOR.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("Administrator " + userId + " is not active.");
        }
    }
}
