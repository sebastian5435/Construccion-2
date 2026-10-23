package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.User;
import application.domain.ports.output.InventoryMovementRepositoryPort;
import application.domain.ports.output.InventoryRepositoryPort;
import application.domain.valueobjects.InventoryMovementType;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StockMovementService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;

    public StockMovementService(InventoryRepositoryPort inventoryRepository,
                                InventoryMovementRepositoryPort movementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    public void reserveForOrder(Order order, User performedBy) {
        Map<OrderItem, Inventory> allocation = new LinkedHashMap<>();
        for (OrderItem item : order.getPhysicalItems()) {
            Inventory inventory = inventoryRepository.findByProduct(item.getProduct().getId()).stream()
                    .filter(candidate -> candidate.canReserve(item.getQuantity()))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotAvailableException(item.getProduct().getId(),
                            "no single warehouse has " + item.getQuantity() + " available units"));
            allocation.put(item, inventory);
        }
        allocation.forEach((item, inventory) -> {
            inventory.reserve(item.getQuantity());
            item.setSourceWarehouse(inventory.getWarehouse());
            inventoryRepository.save(inventory);
            record(inventory, InventoryMovementType.RESERVATION, item.getQuantity(), performedBy, order.getId());
        });
    }

    public void releaseForOrder(Order order, User performedBy) {
        for (OrderItem item : order.getPhysicalItems()) {
            Inventory inventory = requireSourceInventory(item);
            inventory.releaseReservation(item.getQuantity());
            inventoryRepository.save(inventory);
            record(inventory, InventoryMovementType.RESERVATION_RELEASE, item.getQuantity(), performedBy, order.getId());
        }
    }

    public void consumeForOrder(Order order, User performedBy) {
        for (OrderItem item : order.getPhysicalItems()) {
            Inventory inventory = requireSourceInventory(item);
            inventory.consumeReservation(item.getQuantity());
            inventoryRepository.save(inventory);
            record(inventory, InventoryMovementType.SALE_EXIT, item.getQuantity(), performedBy, order.getId());
        }
    }

    public void restockForOrder(Order order, User performedBy) {
        for (OrderItem item : order.getPhysicalItems()) {
            Inventory inventory = requireSourceInventory(item);
            inventory.addStock(item.getQuantity());
            inventoryRepository.save(inventory);
            record(inventory, InventoryMovementType.RETURN, item.getQuantity(), performedBy, order.getId());
        }
    }

    public InventoryMovement record(Inventory inventory, InventoryMovementType type, int quantity,
                                    User performedBy, String reference) {
        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setPerformedBy(performedBy);
        movement.setReference(reference);
        movement.setOccurredAt(LocalDateTime.now());
        return movementRepository.save(movement);
    }

    private Inventory requireSourceInventory(OrderItem item) {
        String productId = item.getProduct().getId();
        String warehouseId = item.getSourceWarehouse().getId();
        return inventoryRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventory", "product=" + productId + ", warehouse=" + warehouseId));
    }
}
