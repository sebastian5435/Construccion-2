package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.WarehouseRequest;
import application.adapters.web.dto.response.WarehouseResponse;
import application.domain.models.Warehouse;
import application.domain.ports.input.WarehouseManagementUseCase;
import application.domain.valueobjects.WarehouseType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WarehouseController {

    private final WarehouseManagementUseCase warehouseManagement;

    public WarehouseController(WarehouseManagementUseCase warehouseManagement) {
        this.warehouseManagement = warehouseManagement;
    }

    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse registerMarketplaceWarehouse(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                                          @Valid @RequestBody WarehouseRequest request) {
        return WarehouseResponse.from(
                warehouseManagement.registerMarketplaceWarehouse(toWarehouse(request), requesterId));
    }

    @PostMapping("/sellers/{sellerId}/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse registerSellerWarehouse(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                                     @PathVariable String sellerId,
                                                     @Valid @RequestBody WarehouseRequest request) {
        return WarehouseResponse.from(
                warehouseManagement.registerSellerWarehouse(toWarehouse(request), sellerId, requesterId));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public WarehouseResponse findWarehouseById(@PathVariable String warehouseId) {
        return WarehouseResponse.from(warehouseManagement.findWarehouseById(warehouseId));
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses(@RequestParam(required = false) WarehouseType type) {
        List<Warehouse> warehouses = type == WarehouseType.MARKETPLACE
                ? warehouseManagement.listMarketplaceWarehouses()
                : warehouseManagement.listAllWarehouses().stream()
                        .filter(warehouse -> type == null || warehouse.getWarehouseType() == type)
                        .toList();
        return warehouses.stream().map(WarehouseResponse::from).toList();
    }

    @GetMapping("/sellers/{sellerId}/warehouses")
    public List<WarehouseResponse> listWarehousesBySeller(@PathVariable String sellerId) {
        return warehouseManagement.listWarehousesBySeller(sellerId).stream().map(WarehouseResponse::from).toList();
    }

    private Warehouse toWarehouse(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.name());
        warehouse.setAddress(request.address());
        return warehouse;
    }
}
