package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.ProductNotAvailableException;
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
import application.domain.services.access.AccessGuard;
import application.domain.services.inventory.StockMovementService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderProcessingService implements OrderProcessingUseCase {

    private final OrderRepositoryPort orderRepository;
    private final InvoiceRepositoryPort invoiceRepository;
    private final ShoppingCartRepositoryPort cartRepository;
    private final StockMovementService stockMovementService;
    private final AccessGuard accessGuard;

    public OrderProcessingService(OrderRepositoryPort orderRepository,
                                  InvoiceRepositoryPort invoiceRepository,
                                  ShoppingCartRepositoryPort cartRepository,
                                  StockMovementService stockMovementService,
                                  AccessGuard accessGuard) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.cartRepository = cartRepository;
        this.stockMovementService = stockMovementService;
        this.accessGuard = accessGuard;
    }

    @Override
    public Order placeOrder(String buyerId, String deliveryAddress) {
        Buyer buyer = (Buyer) accessGuard.requireActiveUser(buyerId, "placeOrder", SystemRole.BUYER);
        if (!buyer.canPurchase()) {
            throw new InvalidOperationException(
                    "Buyer " + buyerId + " has commercial restrictions applied.");
        }
        ShoppingCart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart for buyer", buyerId));
        if (cart.isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot place an order from an empty shopping cart.");
        }
        String address = deliveryAddress == null || deliveryAddress.isBlank()
                ? buyer.getMainAddress()
                : deliveryAddress;
        if (!buyer.hasAddress(address)) {
            throw new InvalidOperationException(
                    "The delivery address must be one of the buyer's registered addresses.");
        }

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setBuyer(buyer);
        order.setItems(copyItems(cart));
        order.setStatus(OrderStatus.CART);
        order.setDeliveryAddress(address);
        order.setTotalAmount(order.calculateTotal());
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        stockMovementService.reserveForOrder(order, buyer);
        order.markAsPendingPayment();
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);
        return savedOrder;
    }

    @Override
    public Order payOrder(String orderId, String buyerId) {
        accessGuard.requireActiveUser(buyerId, "payOrder", SystemRole.BUYER);
        Order order = findOrderById(orderId);
        requireOwner(order, buyerId, "payOrder");
        order.markAsPaid();
        if (!order.requiresShipping()) {
            order.markAsDelivered();
        }
        Order savedOrder = orderRepository.save(order);
        generateInvoice(savedOrder);
        return savedOrder;
    }

    @Override
    public Order cancelOrder(String orderId, String buyerId) {
        User buyer = accessGuard.requireActiveUser(buyerId, "cancelOrder", SystemRole.BUYER);
        Order order = findOrderById(orderId);
        requireOwner(order, buyerId, "cancelOrder");
        order.cancel();
        stockMovementService.releaseForOrder(order, buyer);
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

    private List<OrderItem> copyItems(ShoppingCart cart) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem cartItem : cart.getItems()) {
            if (!cartItem.getProduct().isPublished()) {
                throw new ProductNotAvailableException(cartItem.getProduct().getId(),
                        "product status is " + cartItem.getProduct().getStatus());
            }
            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            items.add(item);
        }
        return items;
    }

    private void requireOwner(Order order, String buyerId, String action) {
        if (!order.isPlacedBy(buyerId)) {
            throw new UnauthorizedRoleException(action, "the buyer who placed the order");
        }
    }

    private Invoice generateInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID().toString());
        invoice.setOrder(order);
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setIssueDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }
}
