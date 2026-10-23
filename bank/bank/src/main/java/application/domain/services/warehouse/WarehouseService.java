package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.input.WarehouseManagementUseCase;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseService implements WarehouseManagementUseCase {

    private final WarehouseRepositoryPort warehouseRepository;
    private final UserRepositoryPort userRepository;
    private final AccessGuard accessGuard;

    public WarehouseService(WarehouseRepositoryPort warehouseRepository,
                            UserRepositoryPort userRepository,
                            AccessGuard accessGuard) {
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public Warehouse registerMarketplaceWarehouse(Warehouse warehouse, String administratorId) {
        accessGuard.requireActiveUser(administratorId, "registerMarketplaceWarehouse", SystemRole.ADMINISTRATOR);
        warehouse.validateRequiredData();
        warehouse.setWarehouseType(WarehouseType.MARKETPLACE);
        warehouse.setSeller(null);
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse registerSellerWarehouse(Warehouse warehouse, String sellerId, String administratorId) {
        accessGuard.requireActiveUser(administratorId, "registerSellerWarehouse", SystemRole.ADMINISTRATOR);
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("User", sellerId));
        if (!user.hasRole(SystemRole.SELLER)) {
            throw new UnauthorizedRoleException("own a seller warehouse", SystemRole.SELLER.name());
        }
        warehouse.validateRequiredData();
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
}
