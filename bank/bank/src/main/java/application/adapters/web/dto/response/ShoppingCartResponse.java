package application.adapters.web.dto.response;

import application.domain.models.ShoppingCart;
import java.util.List;

public record ShoppingCartResponse(
        String id,
        String buyerId,
        List<OrderItemResponse> items,
        double total) {

    public static ShoppingCartResponse from(ShoppingCart cart) {
        return new ShoppingCartResponse(cart.getId(), cart.getBuyer().getIdentifier(),
                cart.getItems().stream().map(OrderItemResponse::from).toList(),
                cart.calculateTotal());
    }
}
