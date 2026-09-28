package application.domain.exceptions;

public class NegativeInventoryException extends DomainException {
    public NegativeInventoryException(String productId, int requested, int available) {
        super("Insufficient inventory for product " + productId
                + ". Requested: " + requested + ", available: " + available
                + ". Negative inventory is not allowed.");
    }
}
