package application.adapters.web.dto.response;

import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;

public record WarehouseResponse(
        String id,
        String name,
        String address,
        WarehouseType warehouseType,
        String sellerId) {

    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getAddress(),
                warehouse.getWarehouseType(),
                warehouse.getSeller() == null ? null : warehouse.getSeller().getIdentifier());
    }
}
