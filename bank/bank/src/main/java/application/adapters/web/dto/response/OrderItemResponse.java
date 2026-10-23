package application.adapters.web.dto.response;

import application.domain.models.OrderItem;
import application.domain.valueobjects.ProductType;

public record OrderItemResponse(
        String productId,
        String productName,
        ProductType productType,
        int quantity,
        double unitPrice,
        double subtotal,
        String sourceWarehouseId) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(),
                item.getProduct().getProductType(), item.getQuantity(), item.getUnitPrice(),
                item.getSubtotal(),
                item.getSourceWarehouse() == null ? null : item.getSourceWarehouse().getId());
    }
}
