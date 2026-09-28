package application.domain.ports.input;

import application.domain.models.ShoppingCart;

/**
 * Manages the shopping cart: temporary selection of products before checkout.
 * Covers business objective OBJ-07.
 */
public interface ShoppingCartUseCase {

    ShoppingCart createCart(String buyerId);

    ShoppingCart addProductToCart(String buyerId, String productId, int quantity);

    ShoppingCart removeProductFromCart(String buyerId, String productId);

    ShoppingCart updateProductQuantity(String buyerId, String productId, int newQuantity);

    ShoppingCart getCartByBuyer(String buyerId);

    void clearCart(String buyerId);
}
