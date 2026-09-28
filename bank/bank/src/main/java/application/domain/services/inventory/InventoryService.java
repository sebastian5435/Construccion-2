package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.NegativeInventoryException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.ports.input.InventoryManagementUseCase;
import application.domain.ports.output.InventoryRepositoryPort;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.ports.output.WarehouseRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InventoryManagementUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;

    public InventoryService(InventoryRepositoryPort inventoryRepository,
                            ProductRepositoryPort productRepository,
                            WarehouseRepositoryPort warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Inventory registerInventory(String productId, String warehouseId, int initialQuantity) {
        requirePositiveOrZero(initialQuantity);
        Product product = requireProduct(productId);
        Warehouse warehouse = requireWarehouse(warehouseId);

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
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory addStock(String productId, String warehouseId, int quantity) {
        requirePositive(quantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory reserveStock(String productId, String warehouseId, int quantity) {
        requirePositive(quantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        int available = inventory.getAvailableQuantity();
        if (quantity > available) {
            throw new NegativeInventoryException(productId, quantity, available);
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory releaseReservedStock(String productId, String warehouseId, int quantity) {
        requirePositive(quantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        if (quantity > inventory.getReservedQuantity()) {
            throw new InvalidOperationException(
                    "Cannot release more than the currently reserved quantity.");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory consumeReservedStock(String productId, String warehouseId, int quantity) {
        requirePositive(quantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        if (quantity > inventory.getReservedQuantity()) {
            throw new InvalidOperationException(
                    "Cannot consume more than the currently reserved quantity.");
        }
        int newQuantity = inventory.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new NegativeInventoryException(productId, quantity, inventory.getQuantity());
        }
        inventory.setQuantity(newQuantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory adjustStock(String productId, String warehouseId, int newQuantity) {
        requirePositiveOrZero(newQuantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        if (newQuantity < inventory.getReservedQuantity()) {
            throw new InvalidOperationException(
                    "Adjusted quantity cannot be less than the currently reserved quantity.");
        }
        inventory.setQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory returnStock(String productId, String warehouseId, int quantity) {
        requirePositive(quantity);
        Inventory inventory = requireInventory(productId, warehouseId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public int checkAvailability(String productId, String warehouseId) {
        Inventory inventory = requireInventory(productId, warehouseId);
        return inventory.getAvailableQuantity();
    }

    @Override
    public List<Inventory> listInventoryByProduct(String productId) {
        return inventoryRepository.findByProduct(productId);
    }

    @Override
    public List<Inventory> listInventoryByWarehouse(String warehouseId) {
        return inventoryRepository.findByWarehouse(warehouseId);
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

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Quantity must be greater than zero.");
        }
    }

    private void requirePositiveOrZero(int quantity) {
        if (quantity < 0) {
            throw new InvalidOperationException("Quantity cannot be negative.");
        }
    }
}
