package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.JpaDomainMapper;
import application.adapters.persistence.jpa.repositories.SpringDataProductRepository;
import application.domain.models.Product;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("!inmemory")
@Transactional
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository repository;
    private final JpaDomainMapper mapper;

    public JpaProductRepositoryAdapter(SpringDataProductRepository repository, JpaDomainMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        repository.save(mapper.toEntity(product));
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return repository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        return repository.findBySellerIdentifier(sellerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
