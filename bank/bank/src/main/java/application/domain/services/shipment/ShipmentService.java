package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.input.ShipmentManagementUseCase;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.ShipmentRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.services.inventory.StockMovementService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShipmentService implements ShipmentManagementUseCase {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final StockMovementService stockMovementService;
    private final AccessGuard accessGuard;

    public ShipmentService(ShipmentRepositoryPort shipmentRepository,
                           OrderRepositoryPort orderRepository,
                           StockMovementService stockMovementService,
                           AccessGuard accessGuard) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.stockMovementService = stockMovementService;
        this.accessGuard = accessGuard;
    }

    @Override
    public Shipment createShipment(String orderId, String logisticsOperatorId) {
        LogisticsOperator operator = requireOperator(logisticsOperatorId, "createShipment");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException(
                    "Shipments can only be created for orders in PAID status. "
                            + "Current status: " + order.getStatus());
        }
        if (!order.requiresShipping()) {
            throw new InvalidOperationException(
                    "Order " + orderId + " only contains digital products and does not require shipping.");
        }
        if (shipmentRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidOperationException(
                    "A shipment already exists for order " + orderId + ".");
        }
        Shipment shipment = new Shipment();
        shipment.setTrackingId(UUID.randomUUID().toString());
        shipment.setOrder(order);
        shipment.setLogisticsOperator(operator);
        shipment.setDestinationAddress(order.getDeliveryAddress());
        shipment.setCreatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    @Override
    public Shipment dispatchShipment(String trackingId, String logisticsOperatorId) {
        LogisticsOperator operator = requireOperator(logisticsOperatorId, "dispatchShipment");
        Shipment shipment = findShipmentByTrackingId(trackingId);
        requireAssignedOperator(shipment, logisticsOperatorId, "dispatchShipment");
        shipment.dispatch();
        stockMovementService.consumeForOrder(shipment.getOrder(), operator);
        orderRepository.save(shipment.getOrder());
        return shipmentRepository.save(shipment);
    }

    @Override
    public Shipment confirmDelivery(String trackingId, String logisticsOperatorId) {
        requireOperator(logisticsOperatorId, "confirmDelivery");
        Shipment shipment = findShipmentByTrackingId(trackingId);
        requireAssignedOperator(shipment, logisticsOperatorId, "confirmDelivery");
        shipment.confirmDelivery();
        orderRepository.save(shipment.getOrder());
        return shipmentRepository.save(shipment);
    }

    @Override
    public Shipment findShipmentByOrder(String orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment for order", orderId));
    }

    @Override
    public Shipment findShipmentByTrackingId(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", trackingId));
    }

    @Override
    public List<Shipment> listShipmentsByOperator(String operatorId) {
        return shipmentRepository.findByOperatorId(operatorId);
    }

    private LogisticsOperator requireOperator(String userId, String action) {
        return (LogisticsOperator) accessGuard.requireActiveUser(userId, action, SystemRole.LOGISTICS_OPERATOR);
    }

    private void requireAssignedOperator(Shipment shipment, String operatorId, String action) {
        if (!shipment.isAssignedTo(operatorId)) {
            throw new UnauthorizedRoleException(action, "the logistics operator assigned to the shipment");
        }
    }
}
