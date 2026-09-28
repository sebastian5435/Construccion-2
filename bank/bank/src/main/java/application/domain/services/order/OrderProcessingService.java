package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.InvalidOrderStateTransitionException;
import application.domain.exceptions.OrderAlreadyFinalizedException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.ports.input.OrderProcessingUseCase;
import application.domain.ports.output.InvoiceRepositoryPort;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.ShoppingCartRepositoryPort;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService implements OrderProcessingUseCase {

    private final OrderRepositoryPort orderRepository;
    private final InvoiceRepositoryPort invoiceRepository;
    private final ShoppingCartRepositoryPort cartRepository;
    private final UserRepositoryPort userRepository;

    public OrderProcessingService(OrderRepositoryPort orderRepository,
                                  InvoiceRepositoryPort invoiceRepository,
                                  ShoppingCartRepositoryPort cartRepository,
                                  UserRepositoryPort userRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order placeOrder(String buyerId) {
        Buyer buyer = requireActiveBuyer(buyerId);
        ShoppingCart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart for buyer", buyerId));

        if (cart.isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot place an order from an empty shopping cart.");
        }

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setBuyer(buyer);
        order.setItems(new ArrayList<>(cart.getItems()));
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(order.calculateTotal());
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return savedOrder;
    }

    @Override
    public Order payOrder(String orderId) {
        Order order = findOrderById(orderId);
        transitionTo(order, OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        generateInvoice(savedOrder);
        return savedOrder;
    }

    @Override
    public Order dispatchOrder(String orderId, String logisticsOperatorId) {
        requireRole(logisticsOperatorId, SystemRole.LOGISTICS_OPERATOR, "dispatchOrder");
        Order order = findOrderById(orderId);
        transitionTo(order, OrderStatus.DISPATCHED);
        return orderRepository.save(order);
    }

    @Override
    public Order deliverOrder(String orderId, String logisticsOperatorId) {
        requireRole(logisticsOperatorId, SystemRole.LOGISTICS_OPERATOR, "deliverOrder");
        Order order = findOrderById(orderId);
        transitionTo(order, OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(String orderId, String buyerId) {
        Order order = findOrderById(orderId);
        if (!order.getBuyer().getIdentifier().equals(buyerId)) {
            throw new UnauthorizedRoleException("cancelOrder", "the buyer who placed the order");
        }
        if (order.isFinalized()) {
            throw new OrderAlreadyFinalizedException(orderId);
        }
        if (order.getStatus() == OrderStatus.DISPATCHED) {
            throw new InvalidOperationException(
                    "Order " + orderId + " has already been dispatched and cannot be cancelled.");
        }
        order.setStatus(OrderStatus.CART);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
    }

    @Override
    public List<Order> listOrdersByBuyer(String buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    @Override
    public Invoice getInvoiceByOrder(String orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice for order", orderId));
    }

    private void transitionTo(Order order, OrderStatus target) {
        if (order.isFinalized()) {
            throw new OrderAlreadyFinalizedException(order.getId());
        }
        if (!isValidTransition(order.getStatus(), target)) {
            throw new InvalidOrderStateTransitionException(order.getStatus(), target);
        }
        order.setStatus(target);
        order.setUpdatedAt(LocalDateTime.now());
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus target) {
        if (current == OrderStatus.CART) {
            return target == OrderStatus.PENDING_PAYMENT;
        }
        if (current == OrderStatus.PENDING_PAYMENT) {
            return target == OrderStatus.PAID;
        }
        if (current == OrderStatus.PAID) {
            return target == OrderStatus.DISPATCHED;
        }
        if (current == OrderStatus.DISPATCHED) {
            return target == OrderStatus.DELIVERED;
        }
        return false;
    }

    private Invoice generateInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID().toString());
        invoice.setOrder(order);
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setIssueDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    private Buyer requireActiveBuyer(String buyerId) {
        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("User", buyerId));
        if (user.getRole() != SystemRole.BUYER) {
            throw new UnauthorizedRoleException("placeOrder", SystemRole.BUYER.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("Buyer " + buyerId + " is not active.");
        }
        return (Buyer) user;
    }

    private User requireRole(String userId, SystemRole requiredRole, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != requiredRole) {
            throw new UnauthorizedRoleException(action, requiredRole.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("User " + userId + " is not active.");
        }
        return user;
    }
}
