package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductEntity;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, String> {

    List<ProductEntity> findByStatus(ProductStatus status);

    List<ProductEntity> findBySellerIdentifier(String sellerId);
}
