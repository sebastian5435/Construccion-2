package application.domain.exceptions;

public class ProductNotAvailableException extends DomainException {
    public ProductNotAvailableException(String productId, String reason) {
        super("Product " + productId + " is not available for purchase: " + reason);
    }
}
