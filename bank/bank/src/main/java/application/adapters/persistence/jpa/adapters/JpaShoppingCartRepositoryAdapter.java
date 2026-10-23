package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataShoppingCartRepository;
import application.domain.models.ShoppingCart;
import application.domain.ports.output.ShoppingCartRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaShoppingCartRepositoryAdapter implements ShoppingCartRepositoryPort {

    private final SpringDataShoppingCartRepository repository;
    private final JpaDomainMapper mapper;

    public JpaShoppingCartRepositoryAdapter(SpringDataShoppingCartRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getId() == null) {
            cart.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(cart));
        return cart;
    }

    @Override
    public Optional<ShoppingCart> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ShoppingCart> findByBuyerId(String buyerId) {
        return repository.findByBuyerIdentifier(buyerId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
