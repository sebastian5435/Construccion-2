package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.input.WarehouseManagementUseCase;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService implements WarehouseManagementUseCase {

    private final WarehouseRepositoryPort warehouseRepository;
    private final UserRepositoryPort userRepository;

    public WarehouseService(WarehouseRepositoryPort warehouseRepository,
                            UserRepositoryPort userRepository) {
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Warehouse registerMarketplaceWarehouse(Warehouse warehouse, String administratorId) {
        requireRole(administratorId, SystemRole.ADMINISTRATOR, "registerMarketplaceWarehouse");
        validateNewWarehouse(warehouse);
        warehouse.setWarehouseType(WarehouseType.MARKETPLACE);
        warehouse.setSeller(null);
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse registerSellerWarehouse(Warehouse warehouse, String sellerId) {
        User user = requireRole(sellerId, SystemRole.SELLER, "registerSellerWarehouse");
        validateNewWarehouse(warehouse);
        warehouse.setWarehouseType(WarehouseType.SELLER);
        warehouse.setSeller((Seller) user);
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse findWarehouseById(String warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
    }

    @Override
    public List<Warehouse> listMarketplaceWarehouses() {
        return warehouseRepository.findByType(WarehouseType.MARKETPLACE);
    }

    @Override
    public List<Warehouse> listWarehousesBySeller(String sellerId) {
        return warehouseRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Warehouse> listAllWarehouses() {
        return warehouseRepository.findAll();
    }

    private void validateNewWarehouse(Warehouse warehouse) {
        if (warehouse.getName() == null || warehouse.getName().isBlank()) {
            throw new InvalidOperationException("Warehouse name is required.");
        }
        if (warehouse.getAddress() == null || warehouse.getAddress().isBlank()) {
            throw new InvalidOperationException("Warehouse address is required.");
        }
    }

    private User requireRole(String userId, SystemRole requiredRole, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != requiredRole) {
            throw new UnauthorizedRoleException(action, requiredRole.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("User " + userId + " is not active.");
        }
        return user;
    }
}
