package application.domain.valueobjects;

public enum OrderStatus {
    CART, PENDING_PAYMENT, PAID, DISPATCHED, DELIVERED, CANCELLED;

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean canTransitionTo(OrderStatus target) {
        switch (this) {
            case CART:
                return target == PENDING_PAYMENT;
            case PENDING_PAYMENT:
                return target == PAID || target == CANCELLED;
            case PAID:
                return target == DISPATCHED;
            case DISPATCHED:
                return target == DELIVERED;
            default:
                return false;
        }
    }
}
