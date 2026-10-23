package application.domain.models;

public class OrderItem {
    private Product product;
    private int quantity;
    private double unitPrice;
    private Warehouse sourceWarehouse;

    public OrderItem() {}

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public Warehouse getSourceWarehouse() { return sourceWarehouse; }
    public void setSourceWarehouse(Warehouse sourceWarehouse) { this.sourceWarehouse = sourceWarehouse; }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public boolean requiresShipping() {
        return product != null && product.isPhysical();
    }
}
