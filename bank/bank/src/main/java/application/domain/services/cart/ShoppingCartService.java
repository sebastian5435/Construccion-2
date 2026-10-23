package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.Buyer;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.ShoppingCart;
import application.domain.ports.input.ShoppingCartUseCase;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.ports.output.ShoppingCartRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.SystemRole;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService implements ShoppingCartUseCase {

    private final ShoppingCartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;
    private final AccessGuard accessGuard;

    public ShoppingCartService(ShoppingCartRepositoryPort cartRepository,
                               ProductRepositoryPort productRepository,
                               AccessGuard accessGuard) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public ShoppingCart createCart(String buyerId) {
        Buyer buyer = requireActiveBuyer(buyerId);
        if (cartRepository.findByBuyerId(buyerId).isPresent()) {
            throw new InvalidOperationException(
                    "Buyer " + buyerId + " already has an active shopping cart.");
        }
        ShoppingCart cart = new ShoppingCart();
        cart.setId(UUID.randomUUID().toString());
        cart.setBuyer(buyer);
        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart addProductToCart(String buyerId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Quantity must be greater than zero.");
        }
        requireActiveBuyer(buyerId);
        ShoppingCart cart = getOrCreateCart(buyerId);
        Product product = requireAvailableProduct(productId);

        OrderItem existingItem = findItem(cart, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
            cart.getItems().add(item);
        }
        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart removeProductFromCart(String buyerId, String productId) {
        requireActiveBuyer(buyerId);
        ShoppingCart cart = getCartByBuyer(buyerId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart updateProductQuantity(String buyerId, String productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidOperationException(
                    "Quantity must be greater than zero. To remove, use removeProductFromCart.");
        }
        requireActiveBuyer(buyerId);
        ShoppingCart cart = getCartByBuyer(buyerId);
        OrderItem item = findItem(cart, productId);
        if (item == null) {
            throw new EntityNotFoundException("Cart item for product", productId);
        }
        item.setQuantity(newQuantity);
        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart getCartByBuyer(String buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart for buyer", buyerId));
    }

    @Override
    public void clearCart(String buyerId) {
        requireActiveBuyer(buyerId);
        ShoppingCart cart = getCartByBuyer(buyerId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private ShoppingCart getOrCreateCart(String buyerId) {
        return cartRepository.findByBuyerId(buyerId).orElseGet(() -> createCart(buyerId));
    }

    private OrderItem findItem(ShoppingCart cart, String productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private Buyer requireActiveBuyer(String buyerId) {
        Buyer buyer = (Buyer) accessGuard.requireActiveUser(buyerId, "shopping cart operations", SystemRole.BUYER);
        if (!buyer.canPurchase()) {
            throw new InvalidOperationException(
                    "Buyer " + buyerId + " has commercial restrictions applied.");
        }
        return buyer;
    }

    private Product requireAvailableProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        if (!product.isPublished()) {
            throw new ProductNotAvailableException(productId,
                    "product status is " + product.getStatus());
        }
        return product;
    }
}
