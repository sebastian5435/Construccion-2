package application.domain.models;

import application.domain.exceptions.InvalidOperationException;
import application.domain.valueobjects.WarehouseType;

public class Warehouse {
    private String id;
    private String name;
    private String address;
    private WarehouseType warehouseType;
    private Seller seller;

    public Warehouse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public WarehouseType getWarehouseType() { return warehouseType; }
    public void setWarehouseType(WarehouseType warehouseType) { this.warehouseType = warehouseType; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public void validateRequiredData() {
        if (name == null || name.isBlank()) {
            throw new InvalidOperationException("Warehouse name is required.");
        }
        if (address == null || address.isBlank()) {
            throw new InvalidOperationException("Warehouse address is required.");
        }
    }
}
