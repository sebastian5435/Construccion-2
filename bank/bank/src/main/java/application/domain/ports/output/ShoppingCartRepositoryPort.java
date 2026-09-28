package application.domain.ports.output;

import application.domain.models.ShoppingCart;
import java.util.Optional;

public interface ShoppingCartRepositoryPort {
    ShoppingCart save(ShoppingCart cart);
    Optional<ShoppingCart> findById(String id);
    Optional<ShoppingCart> findByBuyerId(String buyerId);
    void deleteById(String id);
}
