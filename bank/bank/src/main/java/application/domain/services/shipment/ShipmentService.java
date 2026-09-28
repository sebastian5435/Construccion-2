package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.input.ShipmentManagementUseCase;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.ShipmentRepositoryPort;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService implements ShipmentManagementUseCase {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final UserRepositoryPort userRepository;

    public ShipmentService(ShipmentRepositoryPort shipmentRepository,
                           OrderRepositoryPort orderRepository,
                           UserRepositoryPort userRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Shipment createShipment(String orderId, String logisticsOperatorId) {
        LogisticsOperator operator = requireOperator(logisticsOperatorId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException(
                    "Shipments can only be created for orders in PAID status. "
                            + "Current status: " + order.getStatus());
        }
        if (shipmentRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidOperationException(
                    "A shipment already exists for order " + orderId + ".");
        }

        Shipment shipment = new Shipment();
        shipment.setTrackingId(UUID.randomUUID().toString());
        shipment.setOrder(order);
        shipment.setLogisticsOperator(operator);
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

    private LogisticsOperator requireOperator(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != SystemRole.LOGISTICS_OPERATOR) {
            throw new UnauthorizedRoleException("createShipment",
                    SystemRole.LOGISTICS_OPERATOR.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "Logistics operator " + userId + " is not active.");
        }
        return (LogisticsOperator) user;
    }
}
