package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.input.ProductCatalogUseCase;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements ProductCatalogUseCase {

    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    public ProductService(ProductRepositoryPort productRepository,
                          UserRepositoryPort userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Product createProduct(Product product, String sellerId) {
        Seller seller = requireSeller(sellerId, "createProduct");
        validateProductAttributes(product);
        product.setSeller(seller);
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.PUBLISHED);
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String productId, Product updatedProduct, String sellerId) {
        requireSeller(sellerId, "updateProduct");
        Product existing = findProductById(productId);
        requireOwnership(existing, sellerId);
        if (existing.getStatus() == ProductStatus.DISCONTINUED) {
            throw new InvalidOperationException(
                    "Discontinued products cannot be updated.");
        }
        validateProductAttributes(updatedProduct);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setVariants(updatedProduct.getVariants());
        existing.setProductType(updatedProduct.getProductType());
        return productRepository.save(existing);
    }

    @Override
    public Product publishProduct(String productId, String sellerId) {
        requireSeller(sellerId, "publishProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        if (product.getStatus() == ProductStatus.DISCONTINUED) {
            throw new InvalidOperationException(
                    "Discontinued products cannot be republished.");
        }
        product.setStatus(ProductStatus.PUBLISHED);
        return productRepository.save(product);
    }

    @Override
    public Product suspendProduct(String productId, String sellerId) {
        requireSeller(sellerId, "suspendProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        if (product.getStatus() == ProductStatus.DISCONTINUED) {
            throw new InvalidOperationException(
                    "Discontinued products cannot be suspended.");
        }
        product.setStatus(ProductStatus.SUSPENDED);
        return productRepository.save(product);
    }

    @Override
    public Product discontinueProduct(String productId, String sellerId) {
        requireSeller(sellerId, "discontinueProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        product.setStatus(ProductStatus.DISCONTINUED);
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    @Override
    public List<Product> listPublishedProducts() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }

    @Override
    public List<Product> listProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    private void validateProductAttributes(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidOperationException("Product name is required.");
        }
        if (product.getPrice() < 0) {
            throw new InvalidOperationException("Product price cannot be negative.");
        }
        if (product.getProductType() == null) {
            throw new InvalidOperationException("Product type is required.");
        }
    }

    private void requireOwnership(Product product, String sellerId) {
        if (product.getSeller() == null
                || !sellerId.equals(product.getSeller().getIdentifier())) {
            throw new UnauthorizedRoleException(
                    "modify product " + product.getId(),
                    "the seller who created it");
        }
    }

    private Seller requireSeller(String userId, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != SystemRole.SELLER) {
            throw new UnauthorizedRoleException(action, SystemRole.SELLER.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("Seller " + userId + " is not active.");
        }
        return (Seller) user;
    }
}
