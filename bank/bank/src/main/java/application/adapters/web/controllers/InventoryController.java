package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.InventoryRegistrationRequest;
import application.adapters.web.dto.request.QuantityRequest;
import application.adapters.web.dto.response.InventoryMovementResponse;
import application.adapters.web.dto.response.InventoryResponse;
import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Inventory;
import application.domain.ports.input.InventoryManagementUseCase;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryManagementUseCase inventoryManagement;

    public InventoryController(InventoryManagementUseCase inventoryManagement) {
        this.inventoryManagement = inventoryManagement;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse registerInventory(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                               @Valid @RequestBody InventoryRegistrationRequest request) {
        return InventoryResponse.from(inventoryManagement.registerInventory(
                request.productId(), request.warehouseId(), request.initialQuantity(), requesterId));
    }

    @PostMapping("/{productId}/{warehouseId}/entries")
    public InventoryResponse addStock(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                      @PathVariable String productId,
                                      @PathVariable String warehouseId,
                                      @Valid @RequestBody QuantityRequest request) {
        return InventoryResponse.from(
                inventoryManagement.addStock(productId, warehouseId, request.quantity(), requesterId));
    }

    @PutMapping("/{productId}/{warehouseId}/quantity")
    public InventoryResponse adjustStock(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                         @PathVariable String productId,
                                         @PathVariable String warehouseId,
                                         @Valid @RequestBody QuantityRequest request) {
        return InventoryResponse.from(
                inventoryManagement.adjustStock(productId, warehouseId, request.quantity(), requesterId));
    }

    @PatchMapping("/{productId}/{warehouseId}/damaged")
    public InventoryResponse markAsDamaged(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                           @PathVariable String productId,
                                           @PathVariable String warehouseId) {
        return InventoryResponse.from(inventoryManagement.markAsDamaged(productId, warehouseId, requesterId));
    }

    @PatchMapping("/{productId}/{warehouseId}/available")
    public InventoryResponse markAsAvailable(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                             @PathVariable String productId,
                                             @PathVariable String warehouseId) {
        return InventoryResponse.from(inventoryManagement.markAsAvailable(productId, warehouseId, requesterId));
    }

    @GetMapping("/{productId}/{warehouseId}/availability")
    public Map<String, Integer> checkAvailability(@PathVariable String productId,
                                                  @PathVariable String warehouseId) {
        return Map.of("availableQuantity", inventoryManagement.checkAvailability(productId, warehouseId));
    }

    @GetMapping("/{productId}/{warehouseId}/movements")
    public List<InventoryMovementResponse> listMovements(@PathVariable String productId,
                                                         @PathVariable String warehouseId) {
        return inventoryManagement.listMovements(productId, warehouseId).stream()
                .map(InventoryMovementResponse::from).toList();
    }

    @GetMapping
    public List<InventoryResponse> listInventory(@RequestParam(required = false) String productId,
                                                 @RequestParam(required = false) String warehouseId) {
        List<Inventory> inventories;
        if (productId != null) {
            inventories = inventoryManagement.listInventoryByProduct(productId);
        } else if (warehouseId != null) {
            inventories = inventoryManagement.listInventoryByWarehouse(warehouseId);
        } else {
            throw new InvalidOperationException("Provide either productId or warehouseId to list inventory.");
        }
        return inventories.stream().map(InventoryResponse::from).toList();
    }
}
