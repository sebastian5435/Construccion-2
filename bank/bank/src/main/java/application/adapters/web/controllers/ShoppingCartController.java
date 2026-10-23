package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.CartItemRequest;
import application.adapters.web.dto.request.QuantityRequest;
import application.adapters.web.dto.response.ShoppingCartResponse;
import application.domain.ports.input.ShoppingCartUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartUseCase shoppingCart;

    public ShoppingCartController(ShoppingCartUseCase shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponse createCart(@RequestHeader(RequestHeaders.USER_ID) String buyerId) {
        return ShoppingCartResponse.from(shoppingCart.createCart(buyerId));
    }

    @GetMapping
    public ShoppingCartResponse getCart(@RequestHeader(RequestHeaders.USER_ID) String buyerId) {
        return ShoppingCartResponse.from(shoppingCart.getCartByBuyer(buyerId));
    }

    @PostMapping("/items")
    public ShoppingCartResponse addProduct(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                           @Valid @RequestBody CartItemRequest request) {
        return ShoppingCartResponse.from(
                shoppingCart.addProductToCart(buyerId, request.productId(), request.quantity()));
    }

    @PutMapping("/items/{productId}")
    public ShoppingCartResponse updateQuantity(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                               @PathVariable String productId,
                                               @Valid @RequestBody QuantityRequest request) {
        return ShoppingCartResponse.from(
                shoppingCart.updateProductQuantity(buyerId, productId, request.quantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ShoppingCartResponse removeProduct(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                              @PathVariable String productId) {
        return ShoppingCartResponse.from(shoppingCart.removeProductFromCart(buyerId, productId));
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@RequestHeader(RequestHeaders.USER_ID) String buyerId) {
        shoppingCart.clearCart(buyerId);
    }
}
