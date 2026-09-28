package application.adapters.persistence.inmemory;

import application.domain.models.Product;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryProductRepository implements ProductRepositoryPort {

    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return products.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        return products.values().stream()
                .filter(p -> p.getSeller() != null
                        && sellerId.equals(p.getSeller().getIdentifier()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        products.remove(id);
    }
}
