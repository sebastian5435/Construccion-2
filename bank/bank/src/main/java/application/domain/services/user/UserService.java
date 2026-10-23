package application.domain.services.user;

import application.domain.exceptions.DuplicateEmailException;
import application.domain.exceptions.DuplicateIdentifierException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.input.UserManagementUseCase;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserManagementUseCase {

    private final UserRepositoryPort userRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final AccessGuard accessGuard;

    public UserService(UserRepositoryPort userRepository,
                       WarehouseRepositoryPort warehouseRepository,
                       AccessGuard accessGuard) {
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public Buyer registerBuyer(Buyer buyer) {
        validateNewUser(buyer);
        if (buyer.getMainAddress() == null || buyer.getMainAddress().isBlank()) {
            throw new InvalidOperationException("Main address is required for buyers.");
        }
        buyer.setRole(SystemRole.BUYER);
        buyer.setStatus(UserStatus.ACTIVE);
        if (buyer.getCommercialStatus() == null) {
            buyer.setCommercialStatus(CommercialStatus.ENABLED);
        }
        return (Buyer) userRepository.save(buyer);
    }

    @Override
    public Seller registerSeller(Seller seller, Warehouse firstWarehouse, String administratorId) {
        requireAdministrator(administratorId, "registerSeller");
        validateNewUser(seller);
        if (firstWarehouse == null) {
            throw new InvalidOperationException(
                    "A seller must be registered together with their first warehouse.");
        }
        firstWarehouse.validateRequiredData();
        seller.setRole(SystemRole.SELLER);
        seller.setStatus(UserStatus.ACTIVE);
        Seller savedSeller = (Seller) userRepository.save(seller);

        firstWarehouse.setWarehouseType(WarehouseType.SELLER);
        firstWarehouse.setSeller(savedSeller);
        warehouseRepository.save(firstWarehouse);
        return savedSeller;
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
        if (userId.equals(administratorId)) {
            throw new InvalidOperationException("An administrator cannot block themselves.");
        }
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
        accessGuard.requireActiveUser(userId, action, SystemRole.ADMINISTRATOR);
    }
}
