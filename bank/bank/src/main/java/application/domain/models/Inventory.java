package application.domain.models;

import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.NegativeInventoryException;
import application.domain.valueobjects.InventoryStatus;

public class Inventory {
    private String id;
    private Product product;
    private Warehouse warehouse;
    private int quantity;
    private int reservedQuantity;
    private InventoryStatus status;

    public Inventory() {
        this.status = InventoryStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean isDamaged() {
        return status == InventoryStatus.DAMAGED;
    }

    public boolean canReserve(int units) {
        return !isDamaged() && units > 0 && units <= getAvailableQuantity();
    }

    public void addStock(int units) {
        requirePositive(units);
        this.quantity += units;
    }

    public void reserve(int units) {
        requirePositive(units);
        if (isDamaged()) {
            throw new InvalidOperationException(
                    "Inventory " + id + " is marked as DAMAGED and cannot be reserved.");
        }
        if (units > getAvailableQuantity()) {
            throw new NegativeInventoryException(product.getId(), units, getAvailableQuantity());
        }
        this.reservedQuantity += units;
    }

    public void releaseReservation(int units) {
        requirePositive(units);
        if (units > reservedQuantity) {
            throw new InvalidOperationException(
                    "Cannot release more than the currently reserved quantity.");
        }
        this.reservedQuantity -= units;
    }

    public void consumeReservation(int units) {
        requirePositive(units);
        if (units > reservedQuantity) {
            throw new InvalidOperationException(
                    "Cannot consume more than the currently reserved quantity.");
        }
        if (units > quantity) {
            throw new NegativeInventoryException(product.getId(), units, quantity);
        }
        this.quantity -= units;
        this.reservedQuantity -= units;
    }

    public void adjustTo(int newQuantity) {
        if (newQuantity < 0) {
            throw new InvalidOperationException("Quantity cannot be negative.");
        }
        if (newQuantity < reservedQuantity) {
            throw new InvalidOperationException(
                    "Adjusted quantity cannot be less than the currently reserved quantity.");
        }
        this.quantity = newQuantity;
    }

    public void markAsDamaged() {
        if (reservedQuantity > 0) {
            throw new InvalidOperationException(
                    "Inventory with pending reservations cannot be marked as DAMAGED.");
        }
        this.status = InventoryStatus.DAMAGED;
    }

    public void markAsAvailable() {
        this.status = InventoryStatus.AVAILABLE;
    }

    private void requirePositive(int units) {
        if (units <= 0) {
            throw new InvalidOperationException("Quantity must be greater than zero.");
        }
    }
}
