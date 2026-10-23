package application.adapters.persistence.inmemory;

import application.domain.models.ShoppingCart;
import application.domain.ports.output.ShoppingCartRepositoryPort;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryShoppingCartRepository implements ShoppingCartRepositoryPort {

    private final Map<String, ShoppingCart> carts = new ConcurrentHashMap<>();

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getId() == null) {
            cart.setId(UUID.randomUUID().toString());
        }
        carts.put(cart.getId(), cart);
        return cart;
    }

    @Override
    public Optional<ShoppingCart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }

    @Override
    public Optional<ShoppingCart> findByBuyerId(String buyerId) {
        return carts.values().stream()
                .filter(c -> c.getBuyer() != null
                        && buyerId.equals(c.getBuyer().getIdentifier()))
                .findFirst();
    }

    @Override
    public void deleteById(String id) {
        carts.remove(id);
    }
}
