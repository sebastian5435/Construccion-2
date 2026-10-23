package application.domain.ports.input;

import application.domain.models.Invoice;
import application.domain.models.Order;
import java.util.List;

/**
 * Manages the commercial side of the order lifecycle and automatic invoice generation.
 * Dispatch and delivery of physical orders are handled by ShipmentManagementUseCase.
 * Covers business objectives OBJ-08 and OBJ-09.
 *
 * Lifecycle: CART -> PENDING_PAYMENT -> PAID -> DISPATCHED -> DELIVERED
 *            PENDING_PAYMENT -> CANCELLED
 *            PAID -> DELIVERED (digital-only orders)
 * A finalized (DELIVERED or CANCELLED) order cannot be modified.
 */
public interface OrderProcessingUseCase {

    Order placeOrder(String buyerId, String deliveryAddress);

    Order payOrder(String orderId, String buyerId);

    Order cancelOrder(String orderId, String buyerId);

    Order findOrderById(String orderId);

    List<Order> listOrdersByBuyer(String buyerId);

    Invoice getInvoiceByOrder(String orderId);
}
