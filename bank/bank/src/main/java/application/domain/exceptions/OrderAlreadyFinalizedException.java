package application.domain.exceptions;

public class OrderAlreadyFinalizedException extends DomainException {
    public OrderAlreadyFinalizedException(String orderId) {
        super("Order " + orderId + " is already finalized (DELIVERED or CANCELLED) and cannot be modified.");
    }
}
