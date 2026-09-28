package application.domain.ports.input;

import application.domain.models.Invoice;
import application.domain.models.Order;
import java.util.List;

/**
 * Manages the complete order lifecycle and automatic invoice generation.
 * Covers business objectives OBJ-08 and OBJ-09.
 *
 * Lifecycle: CART -> PENDING_PAYMENT -> PAID -> DISPATCHED -> DELIVERED
 * A finalized (DELIVERED) order cannot be modified.
 */
public interface OrderProcessingUseCase {

    Order placeOrder(String buyerId);

    Order payOrder(String orderId);

    Order dispatchOrder(String orderId, String logisticsOperatorId);

    Order deliverOrder(String orderId, String logisticsOperatorId);

    Order cancelOrder(String orderId, String buyerId);

    Order findOrderById(String orderId);

    List<Order> listOrdersByBuyer(String buyerId);

    Invoice getInvoiceByOrder(String orderId);
}
