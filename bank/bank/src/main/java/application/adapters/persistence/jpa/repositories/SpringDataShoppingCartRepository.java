package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ShoppingCartEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataShoppingCartRepository extends JpaRepository<ShoppingCartEntity, String> {

    Optional<ShoppingCartEntity> findByBuyerIdentifier(String buyerId);
}
