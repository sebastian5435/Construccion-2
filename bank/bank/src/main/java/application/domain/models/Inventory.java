package application.domain.models;

public class Inventory {
    private Product product;
    private Warehouse warehouse;
    private int quantity;

    public Inventory() {}

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public int getQuantity() { return quantity; }
    
    // Domain Validation Rule
    public void setQuantity(int quantity) { 
        if(quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative under any circumstances.");
        }
        this.quantity = quantity; 
    }
}
