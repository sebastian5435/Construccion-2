package application.domain.models;
import application.domain.valueobjects.WarehouseType;

public class Warehouse {
    private String id;
    private String name;
    private WarehouseType warehouseType; 
    public Warehouse() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public WarehouseType getWarehouseType() { return warehouseType; }
    public void setWarehouseType(WarehouseType warehouseType) { this.warehouseType = warehouseType; }
}