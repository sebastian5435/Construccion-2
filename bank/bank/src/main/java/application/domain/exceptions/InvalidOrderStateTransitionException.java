package application.domain.exceptions;

import application.domain.valueobjects.OrderStatus;

public class InvalidOrderStateTransitionException extends DomainException {
    public InvalidOrderStateTransitionException(OrderStatus current, OrderStatus target) {
        super("Invalid order state transition from " + current + " to " + target + ".");
    }
}
