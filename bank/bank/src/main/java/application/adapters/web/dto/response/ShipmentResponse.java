package application.adapters.web.dto.response;

import application.domain.models.Shipment;
import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;

public record ShipmentResponse(
        String trackingId,
        String orderId,
        OrderStatus orderStatus,
        String logisticsOperatorId,
        String destinationAddress,
        LocalDateTime createdAt,
        LocalDateTime dispatchedAt,
        LocalDateTime deliveredAt) {

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(shipment.getTrackingId(), shipment.getOrder().getId(),
                shipment.getOrder().getStatus(), shipment.getLogisticsOperator().getIdentifier(),
                shipment.getDestinationAddress(), shipment.getCreatedAt(),
                shipment.getDispatchedAt(), shipment.getDeliveredAt());
    }
}
