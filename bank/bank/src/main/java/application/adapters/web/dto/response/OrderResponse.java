package application.adapters.web.dto.response;

import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String buyerId,
        OrderStatus status,
        String deliveryAddress,
        double totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getBuyer().getIdentifier(), order.getStatus(),
                order.getDeliveryAddress(), order.getTotalAmount(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt(), order.getUpdatedAt());
    }
}
