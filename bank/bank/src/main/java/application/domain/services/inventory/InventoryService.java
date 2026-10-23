package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.input.InventoryManagementUseCase;
import application.domain.ports.output.InventoryMovementRepositoryPort;
import application.domain.ports.output.InventoryRepositoryPort;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.ports.output.WarehouseRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InventoryManagementUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final StockMovementService stockMovementService;
    private final AccessGuard accessGuard;

    public InventoryService(InventoryRepositoryPort inventoryRepository,
                            InventoryMovementRepositoryPort movementRepository,
                            ProductRepositoryPort productRepository,
                            WarehouseRepositoryPort warehouseRepository,
                            StockMovementService stockMovementService,
                            AccessGuard accessGuard) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockMovementService = stockMovementService;
        this.accessGuard = accessGuard;
    }

    @Override
    public Inventory registerInventory(String productId, String warehouseId, int initialQuantity,
                                       String requesterId) {
        if (initialQuantity < 0) {
            throw new InvalidOperationException("Quantity cannot be negative.");
        }
        Product product = requireProduct(productId);
        User requester = requireInventoryManager(requesterId, product, "registerInventory");
        if (!product.isPhysical()) {
            throw new InvalidOperationException(
                    "Digital products are delivered without inventory and cannot be stocked.");
        }
        product.requireNotDiscontinued("stocked");
        Warehouse warehouse = requireWarehouse(warehouseId);
        if (warehouse.getWarehouseType() == WarehouseType.SELLER
                && !warehouse.getSeller().getIdentifier().equals(product.getSeller().getIdentifier())) {
            throw new InvalidOperationException(
                    "A product can only be stocked in a Marketplace warehouse or in its own Seller's warehouse.");
        }
        if (inventoryRepository.findByProductAndWarehouse(productId, warehouseId).isPresent()) {
            throw new InvalidOperationException(
                    "Inventory for product " + productId
                            + " already exists in warehouse " + warehouseId + ".");
        }
        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID().toString());
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(initialQuantity);
        inventory.setReservedQuantity(0);
        Inventory saved = inventoryRepository.save(inventory);
        if (initialQuantity > 0) {
            stockMovementService.record(saved, InventoryMovementType.ENTRY, initialQuantity, requester, null);
        }
        return saved;
    }

    @Override
    public Inventory addStock(String productId, String warehouseId, int quantity, String requesterId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        User requester = requireInventoryManager(requesterId, inventory.getProduct(), "addStock");
        inventory.addStock(quantity);
        Inventory saved = inventoryRepository.save(inventory);
        stockMovementService.record(saved, InventoryMovementType.ENTRY, quantity, requester, null);
        return saved;
    }

    @Override
    public Inventory adjustStock(String productId, String warehouseId, int newQuantity, String requesterId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        User requester = requireInventoryManager(requesterId, inventory.getProduct(), "adjustStock");
        int difference = Math.abs(newQuantity - inventory.getQuantity());
        inventory.adjustTo(newQuantity);
        Inventory saved = inventoryRepository.save(inventory);
        stockMovementService.record(saved, InventoryMovementType.ADJUSTMENT, difference, requester,
                "new quantity=" + newQuantity);
        return saved;
    }

    @Override
    public Inventory markAsDamaged(String productId, String warehouseId, String requesterId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        requireInventoryManager(requesterId, inventory.getProduct(), "markAsDamaged");
        inventory.markAsDamaged();
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory markAsAvailable(String productId, String warehouseId, String requesterId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        requireInventoryManager(requesterId, inventory.getProduct(), "markAsAvailable");
        inventory.markAsAvailable();
        return inventoryRepository.save(inventory);
    }

    @Override
    public int checkAvailability(String productId, String warehouseId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        return inventory.isDamaged() ? 0 : inventory.getAvailableQuantity();
    }

    @Override
    public List<Inventory> listInventoryByProduct(String productId) {
        return inventoryRepository.findByProduct(productId);
    }

    @Override
    public List<Inventory> listInventoryByWarehouse(String warehouseId) {
        return inventoryRepository.findByWarehouse(warehouseId);
    }

    @Override
    public List<InventoryMovement> listMovements(String productId, String warehouseId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        return movementRepository.findByInventoryId(inventory.getId());
    }

    private User requireInventoryManager(String requesterId, Product product, String action) {
        User requester = accessGuard.requireActiveUser(requesterId, action,
                SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        if (requester.hasRole(SystemRole.SELLER) && !product.isOwnedBy(requesterId)) {
            throw new UnauthorizedRoleException(action, "the seller who owns the product");
        }
        return requester;
    }

    private Inventory requireInventory(String productId, String warehouseId) {
        return inventoryRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventory", "product=" + productId + ", warehouse=" + warehouseId));
    }

    private Product requireProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    private Warehouse requireWarehouse(String warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
    }
}
